package com.ai;

import com.ai.exception.PacketCaptureException;
import com.ai.iface.PacketEngine;
import com.ai.svm.SvmClassifier;
import com.ai.svm.VectorStorage;
import com.ai.utils.concurrent.ProgramConveyr;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by victor on 22.11.15.
 */
public class SvmProgram {
    private static final String PING_FLOOD_FILE = "ping_flood_ddos.vectors";
    private static final String ICMP_FLOOD_FILE = "icmp_flood_ddos.vectors";
    private static final String UDP_FLOOD_FILE = "udp_flood_ddos.vectors";
    private static final String OTHER_DDOS_FILE = "other_ddos.vectors";
    private static Thread conveyer = null;
    private static SvmClassifier pingFloodClassifier;
    private static SvmClassifier icmpFloodClassifier;
    private static SvmClassifier udpFloodClassifier;
    private static SvmClassifier otherDddosClassifier;
    private static VectorStorage pingFloodStorage;
    private static VectorStorage icmpFloodStorage;
    private static VectorStorage udpFloodStorage;
    private static VectorStorage otherDdosStorage;

    private static void init() throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException {
        pingFloodClassifier = new SvmClassifier("ping_flood.model");
        icmpFloodClassifier = new SvmClassifier("icmp_flood.model");
        udpFloodClassifier = new SvmClassifier("udp_flood.model");
        otherDddosClassifier = new SvmClassifier("other_ddos.model");
        File file = new File(PING_FLOOD_FILE);
        if (file.exists() && !file.isDirectory()) {
            pingFloodStorage = (VectorStorage) SerializeUtils.getObject(PING_FLOOD_FILE);
            System.out.println("ping storage " + pingFloodStorage.getVectors().size() + " " + pingFloodStorage.getResults().size());
            for (int i = 0; i < pingFloodStorage.getResults().size(); ++i) {
                System.out.println(pingFloodStorage.getVectors().get(i)[0] + " " + pingFloodStorage.getVectors().get(i)[1] + " " + pingFloodStorage.getVectors().get(i)[2] + " " + pingFloodStorage.getVectors().get(i)[3] + " " + pingFloodStorage.getVectors().get(i)[4] + " " + pingFloodStorage.getVectors().get(i)[5] + " " + pingFloodStorage.getVectors().get(i)[6]);
            }
        } else {
            pingFloodStorage = new VectorStorage(new ArrayList<double[]>(), new ArrayList<Double>());
        }
        file = new File(ICMP_FLOOD_FILE);
        if (file.exists() && !file.isDirectory()) {
            icmpFloodStorage = (VectorStorage) SerializeUtils.getObject(ICMP_FLOOD_FILE);
        } else {
            icmpFloodStorage = new VectorStorage(new ArrayList<double[]>(), new ArrayList<Double>());
        }
        file = new File(UDP_FLOOD_FILE);
        if (file.exists() && !file.isDirectory()) {
            udpFloodStorage = (VectorStorage) SerializeUtils.getObject(UDP_FLOOD_FILE);
        } else {
            udpFloodStorage = new VectorStorage(new ArrayList<double[]>(), new ArrayList<Double>());
        }
        file = new File(OTHER_DDOS_FILE);
        if (file.exists() && !file.isDirectory()) {
            otherDdosStorage = (VectorStorage) SerializeUtils.getObject(OTHER_DDOS_FILE);
        } else {
            otherDdosStorage = new VectorStorage(new ArrayList<double[]>(), new ArrayList<Double>());
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, ClassNotFoundException {
        init();
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
                //   conveyer.interrupt();
                // System.out.println("interrupted");
                //break;
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
                            programConveyr.getEngine().startCapture(maxPack, new OnPacketCaptureEndListenerImpl(programConveyr.getEngine()));
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
                            programConveyr.getEngine().resumeCapture(maxPack, new OnPacketCaptureEndListenerImpl(programConveyr.getEngine()));
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
                            Map<TrafficParams, Double> params = programConveyr.getEngine().getParams();
                            System.out.println("size " + params.size());
                            for (TrafficParams trafficParams : params.keySet()) {
                                System.out.println(trafficParams + " " + params.get(trafficParams));
                            }
                        } catch (PacketCaptureException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    private static class OnPacketCaptureEndListenerImpl implements com.ai.OnPacketCaptureEndListener {
        private PacketEngine packetEngine;

        public OnPacketCaptureEndListenerImpl(PacketEngine packetEngine) {
            this.packetEngine = packetEngine;
        }

        @Override
        public void onEndCapture() {
            try {
                double[] ddosVector = packetEngine.getVectorParams(CommonAttackType.DDOS);
                int choice = 0;
                Scanner in = new Scanner(System.in);
                System.out.print("нажмите 1 для обучения 2 для распознавания ");
                choice = in.nextInt();
                if (choice == 1) {
                    int attackType = 0;
                    System.out.print("выберите тип ddos атаки 1- ping flood, 2- icmp- flood, 3- udp flood, 4 - другой тип, 5- нет атаки ");
                    while ((attackType < 1) || (attackType > 5)) {
                        attackType = in.nextInt();
                    }
                    System.out.println("starting learning");
                    pingFloodStorage.addVector(ddosVector, attackType == 1 ? 1 : -1);
                    icmpFloodStorage.addVector(ddosVector, attackType == 2 ? 1 : -1);
                    udpFloodStorage.addVector(ddosVector, attackType == 3 ? 1 : -1);
                    otherDdosStorage.addVector(ddosVector, attackType == 4 ? 1 : -1);
                    SerializeUtils.saveObject(icmpFloodStorage, ICMP_FLOOD_FILE);
                    SerializeUtils.saveObject(pingFloodStorage, PING_FLOOD_FILE);
                    SerializeUtils.saveObject(udpFloodStorage, UDP_FLOOD_FILE);
                    SerializeUtils.saveObject(otherDdosStorage, OTHER_DDOS_FILE);
                    System.out.println("learning completed");


                } else {
                    pingFloodClassifier.learn(pingFloodStorage.getVectorArray(), pingFloodStorage.getResultsArray());
                    icmpFloodClassifier.learn(icmpFloodStorage.getVectorArray(), icmpFloodStorage.getResultsArray());
                    udpFloodClassifier.learn(udpFloodStorage.getVectorArray(), udpFloodStorage.getResultsArray());
                    otherDddosClassifier.learn(otherDdosStorage.getVectorArray(), otherDdosStorage.getResultsArray());
                    double isPingFlood = pingFloodClassifier.predict(ddosVector);
                    double isIcmpFlood = icmpFloodClassifier.predict(ddosVector);
                    double isUdpFlood = udpFloodClassifier.predict(ddosVector);
                    double isOtherFlood = otherDddosClassifier.predict(ddosVector);
                    boolean isAttack = false;
                    if (isPingFlood == 1) {
                        System.out.println("ping flood");
                        isAttack = true;
                    }
                    if (isIcmpFlood == 1) {
                        System.out.println("icmp flood");
                        isAttack = true;
                    }
                    if (isUdpFlood == 1) {
                        System.out.println("udp flood");
                        isAttack = true;
                    }
                    if (isOtherFlood == 1) {
                        System.out.println("other flood");
                        isAttack = true;
                    }
                    if (!isAttack) {
                        System.out.println("no attacaks " + isPingFlood + " " + isIcmpFlood + " " + isUdpFlood + " " + isOtherFlood);
                    }

                }
                System.out.println("continuing");
            } catch (PacketCaptureException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
