package com.ai.iface;

import com.ai.OnPacketCaptureEndListener;
import com.ai.TrafficParams;
import com.ai.exception.PacketCaptureException;

import java.util.Map;

/**
 *
 */
public interface PacketEngine {
    public Map<TrafficParams, Double> getParams() throws PacketCaptureException;
    public void startCapture(int maxPackets, OnPacketCaptureEndListener listener) throws PacketCaptureException;
    public void stopCapture() throws PacketCaptureException;
    public void reset();
    public void resumeCapture(int maxPackets,OnPacketCaptureEndListener listener) throws PacketCaptureException;
}
