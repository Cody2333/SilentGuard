package com.lowhot.cody.movement.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.lowhot.cody.movement.bean.Config;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by cody_local on 2016/3/9.
 * 工具函数
 */
public class FileUtils {
    public static Context mContext;
    public static String BASE_DIR = "/sdcard/slide";
    public static String MODEL_DIR = BASE_DIR +"/model";
    public static String BASE_TRAIN_DIR = BASE_DIR +"/data/master";
    public static String CONFIG_PATH = BASE_DIR +"/config/app.config.txt";
    private static void initDirs(String... strings) {
        for (String s : strings) {
            File f = new File(BASE_DIR + s);
            if (!f.exists()) {
                f.mkdirs();
            }
        }
    }

    private static void initFiles(String... strings) {
        for (String s : strings) {
            File f = new File(BASE_DIR + s);
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void register(Context context) {
        mContext = context.getApplicationContext();
        //初始化文件路径
        initDirs("/data/master", "/data/guest", "/model", "/config");
        initFiles("/config/app.config.txt");
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
        return cn.getClassName();
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
    public static File createFile(String append_dir, String filename) {
        String dir = BASE_DIR + append_dir;
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

    public static void saveConfig(Config config) {
        File f = new File(BASE_DIR +"/config/app.config.txt");
        //appName:{0},s:{1},t:{2},g:{3},r:{4},n:{5}
        String line = "{0},{1},{2},{3},{4},{5}";
        Object[] array = new Object[]{config.getAppName(),config.getS(),config.getT(),config.getG(),config.getR(),config.getN()};
        String msg = MessageFormat.format(line,array);
        try {
            writeTxt(f,msg);
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
