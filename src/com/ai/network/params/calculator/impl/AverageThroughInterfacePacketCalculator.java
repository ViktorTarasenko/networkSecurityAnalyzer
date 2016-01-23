package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by victor on 23.11.15.
 */
public class AverageThroughInterfacePacketCalculator implements NetworkParameterCalculator {
    @Override
    public double calculate(Collection<Packet> packets, Date start, Date end) {
        if (packets.size() == 0)
            return 0;
        Map<Inet4Address, Double> res = new HashMap<>();
        for (Packet packet : packets) {
            Inet4Address address = null;
            Packet pt = packet;
            while (pt != null) {
                if (pt.getHeader() instanceof IpV4Packet.IpV4Header) {
                    address = ((IpV4Packet.IpV4Header) pt.getHeader()).getDstAddr();
                    break;
                }
                pt = packet.getPayload();
            }
            if (address == null)
                continue;
            if (res.get(address) == null) {
                res.put(address, 0d);
            } else {
                res.put(address, res.get(address) + 1);
            }
        }
        for (Inet4Address address : res.keySet()) {
            res.put(address, res.get(address) / (double) (end.getTime() - start.getTime()));
        }
        double total = 0;
        for (Double val : res.values()) {
            total += val;
        }
        return res.values().size() != 0 ? total / res.values().size() : 0;
    }
}
