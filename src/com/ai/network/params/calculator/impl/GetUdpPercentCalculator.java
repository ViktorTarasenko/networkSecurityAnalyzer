package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.IcmpV4EchoPacket;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.UdpPacket;

import java.util.Collection;
import java.util.Date;

/**
 * Created by victor on 23.11.15.
 */
public class GetUdpPercentCalculator implements NetworkParameterCalculator {
    @Override
    public double calculate(Collection<Packet> packets, Date start, Date end) {
        int packetsCount = packets.size();
        if (packetsCount == 0)
            return 0;
        int targetCount = 0;
        IcmpV4EchoPacket a;
        for (Packet packet : packets) {
            Packet pt = packet;
            while (pt != null) {
                if (pt instanceof UdpPacket) {
                    targetCount++;
                    break;
                }
                pt = pt.getPayload();
            }
        }
        return ((double) targetCount / (double) packetsCount) * 100;
    }
}
