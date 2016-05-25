package com.lowhot.cody.movement.model;

import android.util.Log;

import com.lowhot.cody.movement.bean.Accelerator;
import com.lowhot.cody.movement.bean.Gyroscope;
import com.lowhot.cody.movement.bean.NodeList;
import com.lowhot.cody.movement.entity.SgTrace;
import com.lowhot.cody.movement.entity.SgTraceInfo;
import com.lowhot.cody.movement.svm.src.svm_main;
import com.lowhot.cody.movement.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class ScreenEvent {
    public static String TAG = "ScreenEvent";
    //nodeList 保存了这次触摸事件的所有x,y,pressure等的信息列表
    private NodeList nodeList;
    public String appName;
    private Boolean isAdmin = true;
    private String dir;
    /**
     * 当前匹配的轨迹
     */
    private static SgTrace currentMatchedSgTrace = null;

    public ScreenEvent(NodeList nodeList,
                       LinkedBlockingQueue<Accelerator> acceleratorQueue,
                       LinkedBlockingQueue<Gyroscope> gyroscopeQueue, String appName, String dir, Boolean isAdmin) {
        this.nodeList = nodeList;
        this.appName = appName;
        this.isAdmin = isAdmin;
        this.dir = dir;
        this.nodeList.handleSensor(acceleratorQueue, gyroscopeQueue);
    }


    public long getTime() {
        return nodeList.getDuringTime();
    }


    public String setLine() {

        String line = FileUtils.formatLine(isAdmin, nodeList.getAverageX(), nodeList.getAverageY(), nodeList.getAveragePressure(),
                nodeList.getDuringTime(), this.nodeList.getAverageAccelerator(), this.nodeList.getAverageGyroscope(), appName);
        return line;
    }

    public void saveTrack() throws IOException {
        String trackDir = dir + "/" + "trace";
        File f = FileUtils.createFile(trackDir, appName);
        String line = FileUtils.formatTrackData(nodeList.getMyPoints());
        FileUtils.writeTxt(f, line);
    }

    public void save() throws IOException {
        //保存点击信息至文本文件
        saveDefault();
        //保存轨迹数据到文本文件中
        //saveTrack();
        //保存轨迹信息到数据库中
        //if(isAdmin){
        //    saveInDataBase();
        //}
    }

    private void saveDefault() throws IOException {
        String line = setLine();
        Log.i(TAG, line);
        Log.i(TAG, dir + "/" + appName);
        File file = FileUtils.createFile(dir, appName);
        FileUtils.writeTxt(file, line);
    }

    private void saveInDataBase() {
        try {
            final SgTrace sgTrace = new SgTrace(appName, nodeList);
            sgTrace.save();
            final SgTraceInfo sgTraceInfo = new SgTraceInfo(appName, sgTrace.getId(),new ArrayList<Double>(){}, nodeList.getType(), nodeList.getMethod(), nodeList.getLength(), nodeList.getDuringTime(), 0);
            sgTraceInfo.save();

        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }

    }

    public boolean judge() throws IOException{

        ////TODO
        svm_main m = new svm_main();
        return m.predict(setLine(),appName);
    }


}
