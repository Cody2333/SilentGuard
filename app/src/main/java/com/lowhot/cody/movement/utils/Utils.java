package com.lowhot.cody.movement.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by cody_local on 2016/3/9.
 * 工具函数
 */
public class Utils {
    /**
     * 获取当前时间戳
     * @return
     */
    public static long getTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取 当前的activity名称
     */
    public static String getCurrentActivityName(Context ctx) {
        ActivityManager am = (ActivityManager) ctx.getSystemService(Activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    public static void writeTxt(File filename,String line){
        try{
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(filename,
                            true));
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        }catch (IOException e){
            e.printStackTrace();
        }

    }

    public static String formatLine(Boolean isAdmin, double x, double y, double pressure, long time, double accelerator, double gyroscope, String appName) {
        int isAdminInt = -1;
        if (isAdmin) {
            isAdminInt = 1;
        } else {
            isAdminInt = -1;
        }
        String line = String.valueOf(isAdminInt)
                + " 0:" + x
                + " 1:" + y
                + " 2:" + pressure
                + " 3:" + time
                + " 4:" + accelerator
                + " 5:" + gyroscope
                + " 6:" + appName;
        return line;

    }

    public static String formatLineForOriginData(Context ctx,String name, int type, int code,int value) {

        String line = name + ":"
                + type + " "
                + code + " "
                + value
                + " timestamp:" + Utils.getTimestamp()
                + " appName:"
                + Utils.getCurrentActivityName(ctx);
        return line;

    }

    public static File createFile(String filename){
        File folder = new File("/sdcard/movement");
        if(!folder.exists()){
            folder.mkdir();
        }

        String name = "/sdcard/movement/" + filename + ".txt";
        File outFile = new File(name);
        if (!outFile.exists()) {
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return outFile;
    }

}
