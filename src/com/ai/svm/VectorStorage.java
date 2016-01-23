package com.ai.svm;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by victor on 26.12.15.
 */
public class VectorStorage implements Serializable {
    private ArrayList<double[]> vectors;
    private ArrayList<Double> results;

    public VectorStorage(ArrayList<double[]> vectors, ArrayList<Double> results) {
        this.vectors = vectors;
        this.results = results;
    }

    public VectorStorage() {
    }

    public ArrayList<double[]> getVectors() {
        return vectors;
    }

    public void setVectors(ArrayList<double[]> vectors) {
        this.vectors = vectors;
    }

    public ArrayList<Double> getResults() {
        return results;
    }

    public void setResults(ArrayList<Double> results) {
        this.results = results;
    }

    public double[][] getVectorArray() {
        double[][] result = new double[vectors.size()][];
        for (int i = 0; i < vectors.size(); ++i) {
            result[i] = vectors.get(i);
        }
        return result;
    }

    public double[] getResultsArray() {
        double[] result = new double[results.size()];
        for (int i = 0; i < results.size(); ++i) {
            result[i] = results.get(i);
        }
        return result;
    }

    public void addVector(double[] vector, double value) {
        vectors.add(vector);
        results.add(value);

    }
}
