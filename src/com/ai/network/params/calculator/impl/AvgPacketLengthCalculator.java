package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.Packet;

import java.util.Collection;
import java.util.Date;

/**
 * Created by victor on 22.11.15.
 */
public class AvgPacketLengthCalculator implements NetworkParameterCalculator {
    @Override
    public double calculate(Collection<Packet> packets, Date start, Date end) {
        if (packets.size() == 0) {
            return 0;
        }
        double result = 0;
        for (Packet packet : packets) {
            result+=packet.length();
        }
        result = result / (double) packets.size();
        return result;

    }
}
