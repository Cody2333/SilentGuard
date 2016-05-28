package com.lowhot.cody.movement.svm.src;

import android.util.Log;

import com.lowhot.cody.movement.bean.Config;
import com.lowhot.cody.movement.utils.FileUtils;

import java.io.File;
import java.io.IOException;

public class svm_main {
    public static final String TAG = "SVM";

    public Boolean predict(String predict, String appNmae) throws IOException {
        Log.i(TAG, "....svm_main predicting....");
        File f = new File(FileUtils.MODEL_DIR + "/" + appNmae + ".txt");
        if (!f.exists()) {
            Log.e(TAG, "haven't trained this page yet");
            return true;
        }
        return svm_predict.simplePredict(predict, FileUtils.MODEL_DIR + "/" + appNmae + ".txt");
    }

    public void train(Config config) throws IOException {
        startTrain(FileUtils.BASE_TRAIN_DIR + "/" + config.getAppName(),
                FileUtils.MODEL_DIR + "/" + config.getAppName(), config.getS(), config.getT(), config.getG(), config.getR(), config.getN());
    }

    public static void startTrain(String train, String model, int S, int T, double G, double R, double N) throws IOException {
        System.out.println("....svm_main training now start....");
        String[] arg = {"-s", S + "", "-t", T + "", "-g", G + "", "-r", R + "", "-n", N + "", train, model};
        try {
            svm_train t = new svm_train();
            t.run(arg);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}