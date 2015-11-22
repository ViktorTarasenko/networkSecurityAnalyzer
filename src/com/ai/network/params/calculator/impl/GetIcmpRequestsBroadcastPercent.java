package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;
import java.util.Collection;
import java.util.Date;

/**
 * Created by victor on 23.11.15.
 */
public class GetIcmpRequestsBroadcastPercent implements NetworkParameterCalculator{
    @Override
    public double calculate(Collection<Packet> packets, Date start, Date end) {
        int packetsCount = packets.size();
        int targetCount = 0;
        IcmpV4EchoPacket a;
        for (Packet packet : packets) {
            Packet pt = packet;
            boolean icmp = false;
            while (pt != null) {
                if (pt instanceof IcmpV4EchoPacket) {
                    icmp = true;
                    break;
                }
                pt = packet.getPayload();
            }
            if (icmp) {
               pt = packet;
                while (pt != null) {
                    if (pt.getHeader() instanceof IpV4Packet.IpV4Header) {
                        if (isBroadcast(((IpV4Packet.IpV4Header)pt.getHeader()).getDstAddr())){
                         targetCount++;
                        }
                        targetCount++;
                        break;
                    }
                    pt = packet.getPayload();
                }
            }
        }
        return (double)targetCount / (double)packetsCount;
    }
    private boolean isBroadcast(Inet4Address address) {
        return false;//todo!!
    }
}
