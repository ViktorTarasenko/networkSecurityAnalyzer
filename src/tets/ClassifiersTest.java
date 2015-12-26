package tets;

import com.ai.neural.MultiLayerNeuralNetwork;
import com.ai.neural.NeuralException;
import com.ai.svm.SvmClassifier;
import libsvm.svm;
import org.junit.Test;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 */

public class ClassifiersTest{
    //@Test
    /*public void test() throws NeuralException, TransformerException, ParserConfigurationException {
        MultiLayerNeuralNetwork multiLayerNeuralNetwork = new MultiLayerNeuralNetwork(new MultiLayerNeuralNetwork.Layer(3),new MultiLayerNeuralNetwork.Layer(16),new MultiLayerNeuralNetwork.Layer(4));
        //double[][] big = {{157,57},{160,59},{162,60},{165,61},{168,63},{170,65},{173,67},{175,69},{178,71},{180,72},{183,75},{185,76},{188,79},{190,88},{193,83},{10,1000},{10,50},{150,80},{120,80},};
        //double[][] normal = {{157,54},{160,55},{162,56},{165,58},{168,59},{170,61},{173,63},{175,65},{178,66},{180,68},{183,70},{185,72},{188,74},{190,76},{193,78}};
       // double[][] big = {{157,57},{157,100},{157,200}};
        //double[][] normal = {{157,20},{157,30},{157,40},{157,54}};
        double[][] attack = {{50,1,1},{90,1,2},{80,0,1}};
        for (int i = 0;i < attack.length;++i) {
            multiLayerNeuralNetwork.backPropLearn(attack[i],new double[]{1,0,0,0});
        }
        double[][] hide = {{30,1,1},{60,1,2},{40,0,1}};
        for (int i = 0;i < hide.length;++i) {
            multiLayerNeuralNetwork.backPropLearn(hide[i],new double[]{0,1,0,0});
        }
        double[][] run = {{90,1,7},{60,1,4},{10,0,1}};
        for (int i = 0;i < run.length;++i) {
            multiLayerNeuralNetwork.backPropLearn(run[i],new double[]{0,0,1,0});
        }
        double[][] nothing = {{60,1,0},{100,0,0},{10,0,0},{70,0,0}};
        for (int i = 0;i < nothing.length;++i) {
            multiLayerNeuralNetwork.backPropLearn(nothing[i],new double[]{0,0,0,1});
        }
        multiLayerNeuralNetwork.calculateOutput(new double[]{10,0,0});
        System.out.println(multiLayerNeuralNetw
        ork.getOutput()[0]+" "+multiLayerNeuralNetwork.getOutput()[1]+" "+multiLayerNeuralNetwork.getOutput()[2]+" "+multiLayerNeuralNetwork.getOutput()[3]+"");
        multiLayerNeuralNetwork.save("ddos.neuro");

    }*/
    @Test
    public void test() throws IOException {
        SvmClassifier svmClassifier = new SvmClassifier("/home/victor/test.model");
        double[][] attack = {{50,1,1},{90,1,2},{80,0,1}};
        double[][] hide = {{30,1,1},{60,1,2},{40,0,1}};
        double all[][] = {{0.9,0.9},{0.8,0.8},{0.7,0.7},{0.6,0.6},{0.5,0.5},{0.4,0.4}};
        double[] allValues = {2,1,1,-1,-1,-1};
        svmClassifier.learn(all,allValues);
        System.out.println("result "+svmClassifier.predict(new double[]{1,1}));
    }
}

