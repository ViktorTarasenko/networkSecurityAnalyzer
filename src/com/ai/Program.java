package com.ai;

import com.ai.exception.PacketCaptureException;
import com.ai.iface.PacketEngine;
import com.ai.neural.MultiLayerNeuralNetwork;
import com.ai.neural.NeuralException;
import com.ai.utils.concurrent.ProgramConveyr;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by victor on 22.11.15.
 */
public class Program {
    private static final String DDOS_NEURONET_FILE = "ddos.neuro";
    private static Thread conveyer = null;
    private static MultiLayerNeuralNetwork ddosNetwork;

    private static void init() throws IOException, SAXException, ParserConfigurationException {
        File file = new File(DDOS_NEURONET_FILE);
        if (file.exists() && !file.isDirectory()) {
            ddosNetwork = new MultiLayerNeuralNetwork(DDOS_NEURONET_FILE);
        } else {
            ddosNetwork = new MultiLayerNeuralNetwork(new MultiLayerNeuralNetwork.Layer(7), new MultiLayerNeuralNetwork.Layer(14), new MultiLayerNeuralNetwork.Layer(5));
        }
    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException {
        init();
        System.out.println("started neural program");
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
                //    conveyer.interrupt();
                //  System.out.println("interrupted");
                // break;
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

    private static String printDddosOutputVector(double[] resultVector) {
        int pos = 0;
        for (int i = 1; i < resultVector.length; ++i) {
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
        double[] result = new double[]{0d, 0d, 0d, 0d, 0d};
        result[attackType - 1] = 1;
        return result;
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
                System.out.print("нажмите 1 для обучения 2 для распознавания");
                choice = in.nextInt();
                if (choice == 1) {
                    int attackType = 0;
                    System.out.print("выберите тип ddos атаки 1- ping flood, 2- icmp- flood, 3- udp flood, 4 - другой тип, 5- нет атаки");
                    while ((attackType < 1) || (attackType > 5)) {
                        attackType = in.nextInt();
                    }
                    try {
                        System.out.println("starting learning");
                        ddosNetwork.backPropLearn(ddosVector, convertToDdosOutputVector(attackType));
                        ddosNetwork.save(DDOS_NEURONET_FILE);
                        System.out.println("learning completed");
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (TransformerException e) {
                        e.printStackTrace();
                    } catch (NeuralException e) {
                        e.printStackTrace();
                    }


                } else {
                    try {
                        ddosNetwork.calculateOutput(ddosVector);
                        double[] resultVector = ddosNetwork.getOutput();
                        System.out.println(printDddosOutputVector(resultVector));
                    } catch (NeuralException e) {
                        e.printStackTrace();
                    }
                }
            } catch (PacketCaptureException e) {
                e.printStackTrace();
            }
        }
    }
}
