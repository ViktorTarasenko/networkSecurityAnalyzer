package com.ai;

import com.ai.exception.PacketCaptureException;
import com.ai.iface.PacketEngine;
import com.ai.neural.MultiLayerNeuralNetwork;
import com.ai.neural.NeuralException;
import com.ai.svm.SvmClassifier;
import com.ai.svm.VectorStorage;
import com.ai.utils.concurrent.ProgramConveyr;
import org.pcap4j.core.PcapNetworkInterface;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

/**
 * Created by victor on 22.11.15.
 */
public class SvmProgram {
    private static Thread conveyer = null;
    private static SvmClassifier pingFloodClassifier;
    private static SvmClassifier icmpFloodClassifier;
    private static SvmClassifier udpFloodClassifier;
    private static SvmClassifier otherDddosClassifier;
    private static VectorStorage pingFloodStorage;
    private static VectorStorage icmpFloodStorage;
    private static VectorStorage udpFloodStorage;
    private static VectorStorage otherDdosStorage;
    private static final String PING_FLOOD_FILE = "ping_flood_ddos.vectors";
    private static final String ICMP_FLOOD_FILE = "icmp_flood_ddos.vectors";
    private static final String UDP_FLOOD_FILE = "udp_flood_ddos.vectors";
    private static final String OTHER_FLOOD_FILE = "other_flood_ddos.vectors";
    private static void  init() throws IOException, SAXException, ParserConfigurationException, ClassNotFoundException {
        pingFloodClassifier = new SvmClassifier("ping_flood.model");
        icmpFloodClassifier = new SvmClassifier("icmp_flood.model");
        udpFloodClassifier = new SvmClassifier("udp_flood.model");
        otherDddosClassifier = new SvmClassifier("other_ddos.model");
        File file = new File(PING_FLOOD_FILE);
        if(file.exists() && !file.isDirectory()) {
                pingFloodStorage = (VectorStorage) SerializeUtils.getObject(PING_FLOOD_FILE);
        }
        else {
            pingFloodStorage = new VectorStorage(new ArrayList<double[]>(),new ArrayList<Double>());
        }
         file = new File(ICMP_FLOOD_FILE);
        if(file.exists() && !file.isDirectory()) {
            icmpFloodStorage = (VectorStorage) SerializeUtils.getObject(ICMP_FLOOD_FILE);
        }
        else {
            icmpFloodStorage = new VectorStorage(new ArrayList<double[]>(),new ArrayList<Double>());
        }
        file = new File(UDP_FLOOD_FILE);
        if(file.exists() && !file.isDirectory()) {
            udpFloodStorage = (VectorStorage) SerializeUtils.getObject(UDP_FLOOD_FILE);
        }
        else {
            udpFloodStorage = new VectorStorage(new ArrayList<double[]>(),new ArrayList<Double>());
        }
        file = new File(OTHER_FLOOD_FILE);
        if(file.exists() && !file.isDirectory()) {
           otherDdosStorage = (VectorStorage) SerializeUtils.getObject(OTHER_FLOOD_FILE);
        }
        else {
            otherDdosStorage = new VectorStorage(new ArrayList<double[]>(),new ArrayList<Double>());
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
                            programConveyr.getEngine().startCapture(maxPack,new OnPacketCaptureEndListenerImpl(programConveyr.getEngine()));
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
    private static class OnPacketCaptureEndListenerImpl implements com.ai.OnPacketCaptureEndListener {
        public OnPacketCaptureEndListenerImpl(PacketEngine packetEngine) {
            this.packetEngine = packetEngine;
        }

        private PacketEngine packetEngine;
        @Override
        public void onEndCapture() {
            try {
                double[] ddosVector = packetEngine.getVectorParams(CommonAttackType.DDOS);
                int choice = 0;
                Scanner in = new Scanner(System.in);
                System.out.print("нажмите 1 для обучения 2 для распознавания");
                choice = in.nextInt();
                if (choice == 1) {
                    int attackType = 0;
                    System.out.print("выберите тип ddos атаки 1- ping flood, 2- icmp- flood, 3- udp flood, 4 - другой тип, 5- нет атаки");
                    while ((attackType < 1) || (attackType > 5)){
                        attackType = in.nextInt();
                    }
                        pingFloodStorage.addVector(ddosVector,attackType == 1 ? 1 : -1);
                        icmpFloodStorage.addVector(ddosVector,attackType == 2 ? 1 : -1);
                        udpFloodStorage.addVector(ddosVector,attackType == 3 ? 1 : -1);
                        otherDdosStorage.addVector(ddosVector,attackType == 4 ? 1 : -1);
                        SerializeUtils.saveObject(icmpFloodStorage,ICMP_FLOOD_FILE);
                        SerializeUtils.saveObject(pingFloodStorage,PING_FLOOD_FILE);
                        SerializeUtils.saveObject(udpFloodStorage,UDP_FLOOD_FILE);
                        SerializeUtils.saveObject(pingFloodStorage,PING_FLOOD_FILE);



                }
                else {
                        pingFloodClassifier.learn(pingFloodStorage.getVectorArray(),pingFloodStorage.getResultsArray());
                        icmpFloodClassifier.learn(icmpFloodStorage.getVectorArray(),icmpFloodStorage.getResultsArray());
                        udpFloodClassifier.learn(udpFloodStorage.getVectorArray(),udpFloodStorage.getResultsArray());
                        otherDddosClassifier.learn(otherDdosStorage.getVectorArray(),otherDdosStorage.getResultsArray());
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
                            System.out.println("no flood");
                        }

                }
            } catch (PacketCaptureException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static String printDddosOutputVector(double[] resultVector) {
        int pos = 0;
        for (int i = 1;i < resultVector.length;++i) {
            if (resultVector[i] > resultVector[pos])
                pos = i;
        }
        ++pos;
        if (pos == 1) {
            return "ping flood";
        }
        if (pos == 2) {
            return "icmp flood";
        }
        if (pos == 3) {
            return "udp flood";
        }
        if (pos == 4) {
            return "другой тип ddos атаки";
        }
        if (pos == 5) {
            return "нет ddos атаки";
        }
        return "";
    }

    private static double[] convertToDdosOutputVector(int attackType) {
        double[] result = new double[]{0d,0d,0d,0d,0d};
        result[attackType - 1] = 1;
        return result;
    }
}
