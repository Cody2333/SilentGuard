package com.lowhot.cody.movement.svm.src;

import android.util.Log;

import java.io.IOException;
import java.util.StringTokenizer;

import libsvm.svm;
import libsvm.svm_model;
import libsvm.svm_node;

class svm_predict {

    public static Boolean simplePredict(String input,String modelPath) throws IOException {
        svm_model model = svm.svm_load_model(modelPath);
        String line = input;
        StringTokenizer st = new StringTokenizer(line, " \t\n\r\f:");
        double target = atof(st.nextToken());

        int m = st.countTokens() / 2;
        svm_node[] x = new svm_node[m];
        for (int j = 0; j < m; j++) {
            x[j] = new svm_node();
            x[j].index = atoi(st.nextToken());
            x[j].value = atof(st.nextToken());
        }


        double v;
        v = svm.svm_predict(model, x);
        Log.e("predicted V:",String.valueOf(v));
        if (v >0){
            return true;
        }

        return false;
    }

    private static double atof(String s)
    {
        return Double.valueOf(s).doubleValue();
    }

    private static int atoi(String s)
    {
        return Integer.parseInt(s);
    }



}
