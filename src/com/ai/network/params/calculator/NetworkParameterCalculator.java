package com.ai.network.params.calculator;

import org.pcap4j.packet.Packet;

import java.util.Collection;
import java.util.Date;

/**
 *
 */
public interface NetworkParameterCalculator {
    public double calculate(Collection<Packet> packets, Date start, Date end);
}
