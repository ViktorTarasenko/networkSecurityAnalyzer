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
                ProgramConveyr.put(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            programConveyr.getEngine().startCapture(maxPackets,null);
                        } catch (PacketCaptureException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            if (command == 4) {
                ProgramConveyr.put(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            programConveyr.getEngine().resumeCapture(maxPackets,null);
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
                            Map<TrafficParams,Double> params = programConveyr.getEngine().getParams();
                        } catch (PacketCaptureException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }
}
