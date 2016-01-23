package com.ai;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import com.ai.network.params.calculator.impl.*;

/**
 * Created by victor on 22.11.15.
 */
public enum TrafficParams {
    AVG_PACKET_LENGTH(new AvgPacketLengthCalculator()),
    PACKETS_PER_MILIISONDS(new PacketRateCalculator()),
    AVG_THROUGH_PACKET_INTERFACE(new AverageThroughInterfacePacketCalculator()),
    ICMP_REQ_BROADCAST_PERCENT(new GetIcmpRequestsBroadcastPercent()),
    ICMP_PACKETS_PERCENT(new GetIcmpRequestsPacketsPercent()),
    UDP_PACKETS_PERCENT(new GetUdpPercentCalculator()),
    MEDIANA_THROUGH_INTERFACE_PACKET_CALCULATOR(new MedianaThroughInterfacePacketCalculator());

    private NetworkParameterCalculator calc;

    TrafficParams(NetworkParameterCalculator calc) {
        this.calc = calc;
    }

    public NetworkParameterCalculator getCalc() {
        return calc;
    }
}
