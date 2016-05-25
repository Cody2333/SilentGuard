package com.lowhot.cody.movement.svm.src;

import android.util.Log;

import com.lowhot.cody.movement.bean.Config;
import com.lowhot.cody.movement.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class svm_main {
    private static ArrayList<String> fileList = new ArrayList<>();
    private static ArrayList<String> foldList = new ArrayList<>(fileList);
    private static String trainPath = FileUtils.BASE_DIR + "/master/1458716554993/com.taobao.taobao.txt";
    private static String outputPath = FileUtils.BASE_DIR + "/output.txt";
    private static String modelPath = FileUtils.BASE_DIR + "/model/model_r.txt";
    public static final String TAG = "SVM";

    public Boolean predict(String predict, String appNmae) throws IOException {
        Log.i(TAG, "....svm_main predicting....");
        return svm_predict.simplePredict(predict, FileUtils.MODEL_DIR + "/" + appNmae + ".txt");
    }

    public void train(Config config) throws IOException {
        startTrain(FileUtils.BASE_TRAIN_DIR + "/" + config.getAppName(),
                FileUtils.MODEL_DIR + "/" + config.getAppName(), config.getS(), config.getT(), config.getG(), config.getR(), config.getN());
    }

    public static void testEach(String predictPathEach) throws IOException {
        fileList.clear();
        getDirectory(predictPathEach);
        for (String F : fileList) {
            if (F.substring(F.length() - 10).equals(trainPath.substring(trainPath.length() - 10))) {
                startPredict(F, modelPath, outputPath);
                System.out.println();
            }
        }
    }

    public static void getDirectory(String path) throws IOException {
        File root = new File(path);
        File[] files = root.listFiles();
        for (File file : files) {
            if (file.isDirectory())
                foldList.add(file.getAbsolutePath());
            else
                fileList.add(file.getAbsolutePath());
        }
    }

    public static void startTrain(String train, String model, int S, int T, double G, double R, double N) throws IOException {
        System.out.println("....svm_main training now start....");
        String[] arg = {"-s", S + "", "-t", T + "", "-g", G + "", "-r", R + "", "-n", N + "", train, model};
        try{
            svm_train t = new svm_train();
            t.run(arg);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static double startPredict(String predict, String model, String outputs) throws IOException {
        double proportion;
        String[] parg = {predict, model, outputs};
        proportion = svm_predict.main(parg);
        return proportion;
    }
}