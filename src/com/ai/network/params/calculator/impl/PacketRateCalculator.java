package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.Packet;

import java.util.Collection;
import java.util.Date;

/**
 *
 */
public class PacketRateCalculator implements NetworkParameterCalculator {
    @Override
    public double calculate(Collection<Packet> packets, Date start, Date end) {
        return (double) packets.size() / (double)(end.getTime() - start.getTime());
    }
}
