package com.lowhot.cody.movement.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by cody_local on 2016/3/9.
 * 工具函数
 */
public class FileUtils {
    public static Context mContext;
    public static String BASE_DIR = "/sdcard/slide";

    private static void initDirs(String ...strings){
        for (String s : strings){
            File f = new File(BASE_DIR+s);
            if(!f.exists()){
                f.mkdirs();
            }
        }
    }

    public static void register(Context context) {
        mContext = context.getApplicationContext();
        //初始化文件路径
        initDirs("/master","/guest","/model","/config");

    }

    /**
     * 获取当前时间戳
     *
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
        return cn.getPackageName();
    }

    public static void writeTxt(File filename, String line) throws IOException {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(
                    new FileWriter(filename,
                            true));
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
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
                + " 5:" + gyroscope;
        return line;

    }

    public static String formatLineForOriginData(Context ctx, String name, int type, int code, int value) {

        String line = name + ":"
                + type + " "
                + code + " "
                + value
                + " timestamp:" + FileUtils.getTimestamp()
                + " appName:"
                + FileUtils.getCurrentActivityName(ctx);
        return line;

    }

    /**
     * 创建文件
     *
     * @param filename
     * @return
     */
    public static File createFile(String filename) {
        String name = BASE_DIR + "/" + filename + ".txt";
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

    /**
     * 创建文件
     *
     * @param filename
     * @return
     */
    public static File createFile(String append_dir,String filename) {
        String dir = BASE_DIR+append_dir;
        initDirs(append_dir);
        String name = dir + "/" + filename + ".txt";
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
