package com.ai.network.params.calculator.impl;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import org.pcap4j.packet.IpV4Packet;
import org.pcap4j.packet.Packet;

import java.net.Inet4Address;
import java.util.*;

/**
 * Created by victor on 23.11.15.
 */
public class MedianaThroughInterfacePacketCalculator implements NetworkParameterCalculator {
    @Override
    public double calculate(Collection<Packet> packets, Date start, Date end) {
        Map<Inet4Address,Double> res = new HashMap<>();
        for (Packet packet : packets) {
            Inet4Address address = null;
            Packet pt = packet;
            while (pt != null) {
                if (pt.getHeader() instanceof IpV4Packet.IpV4Header) {
                    address = ((IpV4Packet.IpV4Header)pt.getHeader()).getDstAddr();
                    break;
                }
                pt = pt.getPayload();
            }
            if (address == null)
                continue;
            if (res.get(address) == null) {
                res.put(address,0d);
            }
            else {
                res.put(address,res.get(address) + 1);
            }
        }
        for (Inet4Address address : res.keySet()){
            res.put(address,res.get(address) / (double)(end.getTime() - start.getTime()));
        }
        List<Double> values = new ArrayList<Double>(res.values());
        if (values.size() ==0)
            return 0;
        Collections.sort(values);
        if (values.size() == 0) {
            return 0;
        }
        if (values.size() % 2 == 0) {
            return  (values.get(values.size() / 2) + values.get(values.size() / 2 - 1)) / 2;
        }
        else {
            return values.get(values.size() / 2);
        }
    }
}
