package com.lowhot.cody.movement.svm.src;

import android.util.Log;

import com.lowhot.cody.movement.bean.Config;
import com.lowhot.cody.movement.utils.FileUtils;

import java.io.IOException;


public class svm_main {
    public static final String TAG = "SVM";

    public void train(Config config) throws IOException {
        startTrain(FileUtils.BASE_TRAIN_DIR + "/" + config.getAppName(),
                FileUtils.MODEL_DIR + "/" + config.getAppName(), config.getS(), config.getT(), config.getG(), config.getR(), config.getN());
    }

    public Boolean predict(String predict,String appNmae) throws IOException {
        Log.i(TAG,"....svm_main predicting....");
        return svm_predict.simplePredict(predict, FileUtils.MODEL_DIR+"/"+appNmae+".txt");
    }


    public static void startTrain(String train, String model, int S, int T, double G, double R, double N) throws IOException {
        Log.i(TAG,"....svm_main training now start....");
        String[] arg = {"-s", S + "", "-t", T + "", "-g", G + "", "-r", R + "", "-n", N + "", train, model};
        svm_train.main(arg);
    }

}