package com.ai.svm;

import libsvm.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 */
public class SvmClassifier {
    private String filename;
    public SvmClassifier(String filename) throws IOException {
        param = new svm_parameter();
        param.svm_type = svm_parameter.C_SVC;
        param.kernel_type = svm_parameter.RBF;
        param.degree = 3;
        param.gamma = 0.10000000000000001;	// 1/num_features
        param.coef0 = 0;
        param.nu = 0.5;
        param.cache_size = 100;
        param.C = 1;
        param.eps = 1e-3;
        param.p = 0.1;
        param.shrinking = 1;
        param.probability = 0;
        param.nr_weight = 0;
        param.weight_label = new int[3];
        param.weight = new double[3];
        this.filename = filename;
        File f = new File(filename);
        if(f.exists() && !f.isDirectory()) {
           readFromFile(filename);
            System.out.println("read file");
        }

    }
    public void save(String filename) throws IOException {
         svm.svm_save_model(filename,model);
    }
    public void learn(double[][] input,double[] requiredOutput) throws IOException {
        if (model != null) {
            //System.out.println("indice "+model.sv_indices.length);

        }

        svm_problem svm_problem = transformInput(input,requiredOutput);
        model = svm.svm_train(svm_problem,this.param);
       // System.out.println("indice "+model.sv_indices[5]);
        save(filename);

    }
    private svm_problem transformInput(double[][] input,double[] requiredOutput) {
        svm_problem svm_problem = new svm_problem();
        svm_problem.l = input.length;
        svm_problem.y = requiredOutput;
        svm_problem.x = new svm_node[svm_problem.l][];
        for (int i = 0;i < svm_problem.l;++i) {
            svm_problem.x[i] = new svm_node[input[i].length];
            for (int j = 0;j < input[i].length;++j) {
                svm_node svm_node = new svm_node();
                svm_node.index = j + 1;
                svm_node.value = input[i][j];
                svm_problem.x[i][j] = svm_node;
            }

        }
        return svm_problem;
    }
    private void readFromFile(String filename) throws IOException {
      model =  svm.svm_load_model(filename);
    }
    public double predict(double[] input) {
        if (model == null) {
            throw new RuntimeException("model not loaded");
        }
        svm_node[] nodes = new svm_node[input.length];
        for (int i = 0;i < input.length; ++i) {
            nodes[i] = new svm_node();
            nodes[i].index = i+1;
            nodes[i].value = input[i];
        }
        return svm.svm_predict(model,nodes);

    }

    private svm_parameter param;
    private svm_model model;


}
