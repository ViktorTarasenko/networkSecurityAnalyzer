package com.ai.impl;

import com.ai.CommonAttackType;
import com.ai.OnPacketCaptureEndListener;
import com.ai.TrafficParams;
import com.ai.exception.PacketCaptureException;
import com.ai.exception.WrongEgineStateException;
import com.ai.iface.PacketEngine;
import com.ai.utils.concurrent.ProgramConveyr;
import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.*;

/**
 *
 */
public class Pcap4jPacketEngine implements PacketEngine {
    private Date start;
    private Date end;
    private List<Packet> packets = new ArrayList<Packet>();
    private PcapNetworkInterface networkInterface;
    private PcapHandle handle;
    @Override
    public Map<TrafficParams, Double> getParams() throws PacketCaptureException {
        if ((handle != null) && (handle.isOpen())) {
            throw new WrongEgineStateException();
        }
        Map<TrafficParams,Double> result = new HashMap<TrafficParams,Double>();
        for (TrafficParams param : TrafficParams.values()) {
            result.put(param,param.getCalc().calculate(packets,start,end));
        }
        return result;
    }

    @Override
    public void startCapture(int maxPackets, OnPacketCaptureEndListener listener) throws PacketCaptureException {
        reset();
        resumeCapture(maxPackets,listener);
    }

    @Override
    public void stopCapture() throws PacketCaptureException {
        if (handle == null)
            return;
        if (handle.isOpen()) {
            try {
                handle.breakLoop();
            } catch (NotOpenException e) {
                throw  new PacketCaptureException(e);
            }
        }
        handle.close();

    }

    @Override
    public void reset() {
        networkInterface = null;
        packets.clear();
    }


    @Override
    public void resumeCapture(int maxPackets,final OnPacketCaptureEndListener listener) throws PacketCaptureException {
        if ((handle != null) && (handle.isOpen())) {
            stopCapture();
        }
        start = null;
        end = null;
        try {
            networkInterface = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            throw new PacketCaptureException(e);
        }
        if (networkInterface == null) {
            throw new PacketCaptureException("network inteface not selected!");
        }
        try {
            handle = networkInterface.openLive(65535, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, 9);
        } catch (PcapNativeException e) {
            throw new PacketCaptureException(e);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    handle.loop(maxPackets, new PacketListener() {
                        @Override
                        public void gotPacket(Packet packet) {
                            System.out.println(""+packet);
                            packets.add(packet);
                            ProgramConveyr.put(new Runnable() {
                                @Override
                                public void run() {
                                    if (start == null) {
                                        start = new Date();
                                    }
                                    end = new Date();
                                }
                            });
                        }
                    });
                } catch (InterruptedException e) {
                   e.printStackTrace();
                } catch (PcapNativeException e) {
                   e.printStackTrace();
                } catch (NotOpenException e) {
                   e.printStackTrace();
                }
                finally {
                    ProgramConveyr.put(
                    new Runnable() {
                        public void run(){
                    try {
                        stopCapture();
                        if (listener != null) {
                            listener.onEndCapture();
                        }
                    } catch (PacketCaptureException e1) {
                        e1.printStackTrace();
                    }}
                    });
                }
            }
        }).start();


    }

    @Override
    public double[] getVectorParams(CommonAttackType attackType) throws PacketCaptureException {
        Map<TrafficParams,Double> paramsMap = getParams();
        if (attackType == CommonAttackType.DDOS) {
            return new double[]{paramsMap.get(TrafficParams.AVG_PACKET_LENGTH),paramsMap.get(TrafficParams.AVG_THROUGH_PACKET_INTERFACE),paramsMap.get(TrafficParams.ICMP_PACKETS_PERCENT),paramsMap.get(TrafficParams.ICMP_REQ_BROADCAST_PERCENT),paramsMap.get(TrafficParams.MEDIANA_THROUGH_INTERFACE_PACKET_CALCULATOR),paramsMap.get(TrafficParams.PACKETS_PER_MILIISONDS),paramsMap.get(TrafficParams.UDP_PACKETS_PERCENT)};
        }
        return new double[0];
    }

    private class PcapEnginePacketListener implements PacketListener{

        @Override
        public void gotPacket(Packet packet) {
            System.out.println(""+packet);
            packets.add(packet);
            ProgramConveyr.put(new Runnable() {
                @Override
                public void run() {
                    if (start == null) {
                        start = new Date();
                    }
                    end = new Date();
                }
            });
        }
    }
}
