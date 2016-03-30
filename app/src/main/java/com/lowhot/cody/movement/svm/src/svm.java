package com.lowhot.cody.movement.svm.src;

import com.lowhot.cody.movement.utils.FileUtils;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.lang.String;



public class svm {

    private static ArrayList<String> fileList = new ArrayList<>();
    private static ArrayList<String> foldList = new ArrayList<>(fileList);
    private static String trainPath = FileUtils.BASE_DIR+"/master/1458716554993/com.taobao.taobao.txt";
    private static String outputPath = FileUtils.BASE_DIR+"/output.txt";
    private static String modelPath =  FileUtils.BASE_DIR +"/model/model_r.txt";


    public void train() throws IOException{

        double g,n;
        int s,t,r;
        s = 2;
        t = 2;
        g = 0.00001;
        r = 1;
        n = 0.19;

        startTrain(trainPath,modelPath,s,t,g,r,n);

    }

    public static void testEach(String predictPathEach) throws IOException{

        fileList.clear();
        getDirectory(predictPathEach);

        for (String F : fileList) {
            if (F.substring(F.length()-10).equals(trainPath.substring(trainPath.length()-10))){
                startPredict(F,modelPath,outputPath);
                System.out.println();
            }
        }
    }

    public static void getDirectory(String path) throws IOException{
        File root = new File(path);
        File[] files = root.listFiles();
        for (File file:files){
            if(file.isDirectory())
                foldList.add(file.getAbsolutePath());
            else
                fileList.add(file.getAbsolutePath());
        }
    }



    public static void startTrain(String train,String model,int S,int T,double G,double R,double N) throws IOException{
        System.out.println("....svm training now start....");

        String[] arg = {"-s",S+"","-t",T+"","-g",G+"","-r",R+"","-n",N+"",train,model};
        svm_train.main(arg);
    }

    public static double startPredict(String predict,String model,String outputs) throws IOException{
        double proportion;

        String[] parg = {predict,model,outputs};
        proportion = svm_predict.main(parg);
        return proportion;
    }
}