package com.ai;

import com.ai.network.params.calculator.NetworkParameterCalculator;
import com.ai.network.params.calculator.impl.AvgPacketLengthCalculator;

/**
 * Created by victor on 22.11.15.
 */
public enum  TrafficParams {
    AVG_PACKET_LENGTH(new AvgPacketLengthCalculator());

    TrafficParams(NetworkParameterCalculator calc) {
        this.calc = calc;
    }
    private NetworkParameterCalculator calc;

    public NetworkParameterCalculator getCalc() {
        return calc;
    }
}
