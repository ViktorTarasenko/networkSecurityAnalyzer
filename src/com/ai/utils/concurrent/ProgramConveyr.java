package com.ai.utils.concurrent;

import com.ai.iface.PacketEngine;
import com.ai.impl.Pcap4jPacketEngine;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by victor on 22.11.15.
 */
public class ProgramConveyr implements Runnable {
    public static final BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();
    PacketEngine engine = new Pcap4jPacketEngine();

    public static void put(Runnable runnable) {
        queue.add(runnable);
    }

    public PacketEngine getEngine() {
        return engine;
    }

    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                queue.take().run();
                System.out.println("took");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
