package com.ai.neural;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 */
public class MultiLayerNeuralNetwork {
    public MultiLayerNeuralNetwork(Layer... layers) {
        for (Layer layer : layers) {
            this.layers.add(layer);
        }
        int length =0;
        for (int i =1;i<this.layers.size();++i) {
            length += this.layers.get(i-1).dimension*this.layers.get(i).dimension;
        }
        weights = new double[length];
        for (int i = 0;i< length;++i) {
            weights[i] = 0;
        }

    }
    private boolean initialized = false;
    private int calcWeightPosition(int layerFrom,int layerTo,int posFrom,int posTo){
        int first = 0;
        int second = 0;
        int third = 0;
        for (int i = 1;i<=layerFrom;++i) {
            first += this.layers.get(i-1).dimension*this.layers.get(i).dimension;
        }
        second = posFrom*this.layers.get(layerTo).dimension;
        third = posTo;
        return first + second + third;
    }
    public void calculateOutput(double[] vector) throws NeuralException {
        if (vector.length != layers.get(0).dimension){
            throw new NeuralException("vector input size doesn't match");
        }
        System.arraycopy(vector,0,layers.get(0).getVector(),0,vector.length);
        for (int i = 1;i<layers.size();++i) {
            for (int j = 0; j < layers.get(i).dimension;++j){
                double newValue = 0;
                for (int k = 0; k < layers.get(i-1).dimension;++k){
                    newValue += layers.get(i-1).getVector()[k]*weights[calcWeightPosition(i-1,i,k,j)];
                }
                newValue = activationFunction(newValue);
                layers.get(i).getVector()[j] = newValue;
            }
        }
    }
    public void backPropLearn(double[] vector,double[] requiredOutput) throws NeuralException {
        if (vector.length != layers.get(0).dimension){
            throw new NeuralException("vector input size doesn't match");
        }
        if (requiredOutput.length != layers.get(layers.size() - 1).dimension){
            throw new NeuralException("vector output size doesn't match");
        }
        if (!initialized) {
            initWeights();
            initLearningSpeeds();
            initialized = true;
        }
        calculateOutput(vector);
        double[] sigmas = null;
        double[] prevSigmas = null;
        for (int i = layers.size() -1 ;i>0;--i) {
            if (i != layers.size() - 1) {
              prevSigmas = sigmas;
            }
            sigmas = new double[layers.get(i).dimension];
            if (i == layers.size() - 1) {
                for (int j = 0; j < layers.get(i).dimension;++j){
                    sigmas[j] = -(requiredOutput[j] - layers.get(i).getVector()[j]) * layers.get(i).getVector()[j] * (1 - layers.get(i).getVector()[j]);
                }
            }
            else {
                for (int j = 0; j < layers.get(i).dimension;++j){
                    double summa = 0;
                    for (int k = 0; k < layers.get(i+1).dimension;++k){
                       summa+= prevSigmas[k]*weights[calcWeightPosition(i,i+1,j,k)];
                    }
                    sigmas[j] = summa*(1-layers.get(i).getVector()[j])*layers.get(i).getVector()[j];

                }
            }
            for (int j = 0; j < layers.get(i).dimension;++j){
                for (int k = 0; k < layers.get(i-1).dimension;++k){
                    double proizv = sigmas[j]*layers.get(i-1).getVector()[k];
                    weights[calcWeightPosition(i-1,i,k,j)] = weights[calcWeightPosition(i-1,i,k,j)] - layers.get(i).learningSpeed*proizv;
                }
            }
        }


    }
    public double activationFunction(double val) {
        return 1/(1+Math.exp(-0.5*val));
        //return val < 0 ? 0 : 1;
    }


    private void initWeights() {
        Random random = new SecureRandom();
        for (int i = 1;i<layers.size();++i) {
            double rangeMax = 1 / Math.sqrt(layers.get(i-1).dimension);
            double rangeMin = - rangeMax;
            for (int j = 0; j < layers.get(i).dimension;++j){
                for (int k = 0; k < layers.get(i-1).dimension;++k){
                    double weight = rangeMin + (rangeMax - rangeMin) * random.nextDouble();
                    weights[calcWeightPosition(i-1,i,k,j)] = weight;
                    System.out.println(calcWeightPosition(i-1,i,k,j)+" "+weights[calcWeightPosition(i-1,i,k,j)]);
                }
            }
        }
    }

    private void initLearningSpeeds() {
        for (int i = 1; i < layers.size(); ++i) {
            layers.get(i).setLearningSpeed(1 / Math.sqrt(layers.get(i - 1).dimension));
        }
    }

    private List<Layer> layers = new ArrayList<Layer>();
    private double[] weights;

    public double[] getOutput() {
        return layers.get(layers.size() - 1).getVector();
    }

    public static class Layer {
        public Layer(int dimension) {
            this.dimension = dimension;
            vector = new double[dimension];
            learningSpeed = 0.5;
        }
        private double learningSpeed;
        private int dimension;
        public double[] vector;

        public int getDimension() {
            return dimension;
        }

        public void setDimension(int dimension) {
            this.dimension = dimension;
        }

        public double[] getVector() {
            return vector;
        }

        public void setVector(double[] vector) {
            this.vector = vector;
        }

        public void setLearningSpeed(double learningSpeed) {
            this.learningSpeed = learningSpeed;
        }

        public double getLearningSpeed() {
            return learningSpeed;
        }
    }
}
