package com.ai;

import com.ai.exception.PacketCaptureException;
import com.ai.utils.concurrent.ProgramConveyr;
import org.pcap4j.core.PcapNetworkInterface;

import java.util.Map;
import java.util.Scanner;

/**
 * Created by victor on 22.11.15.
 */
public class Program {
    private static Thread conveyer = null;

    public static void main(String[] args) {
        int command = 1;
        int maxPackets = 0;
        Scanner in = new Scanner(System.in);
        ProgramConveyr programConveyr = new ProgramConveyr();
        conveyer = new Thread(programConveyr);
        conveyer.start();
        while (true) {
            System.out.println("enter command");
            command = in.nextInt();
            if (command == 1) {
                conveyer.interrupt();
                break;
            }
            if (command == 2) {
                ProgramConveyr.put(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            programConveyr.getEngine().stopCapture();
                        } catch (PacketCaptureException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (command == 3) {
                System.out.println("enter packets number");
                maxPackets = in.nextInt();
                final int maxPack = maxPackets;
                ProgramConveyr.put(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            programConveyr.getEngine().startCapture(maxPack,null);
                        } catch (PacketCaptureException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (command == 4) {
                System.out.println("enter packets number");
                maxPackets = in.nextInt();
                final int maxPack = maxPackets;
                ProgramConveyr.put(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            programConveyr.getEngine().resumeCapture(maxPack,null);
                        } catch (PacketCaptureException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (command == 5) {
                ProgramConveyr.put(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.out.println("five ");
                            Map<TrafficParams,Double> params = programConveyr.getEngine().getParams();
                            System.out.println("size "+params.size());
                            for (TrafficParams trafficParams : params.keySet()) {
                                System.out.println(trafficParams+" "+params.get(trafficParams));
                            }
                        } catch (PacketCaptureException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
