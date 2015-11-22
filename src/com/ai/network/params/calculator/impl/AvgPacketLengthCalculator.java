package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.Packet;

import java.util.Collection;

/**
 * Created by victor on 22.11.15.
 */
public class AvgPacketLengthCalculator implements NetworkParameterCalculator {
    @Override
    public double calculate(Collection<Packet> packets) {
        return 0;
    }
}
