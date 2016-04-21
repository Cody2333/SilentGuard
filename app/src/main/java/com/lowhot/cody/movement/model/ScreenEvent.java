package com.lowhot.cody.movement.model;

import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowhot.cody.movement.bean.Accelerator;
import com.lowhot.cody.movement.bean.Gyroscope;
import com.lowhot.cody.movement.bean.MyPointList;
import com.lowhot.cody.movement.bean.NodeList;
import com.lowhot.cody.movement.entity.SgTrace;
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
    ArrayList<Accelerator> acceleratorQueue = new ArrayList<>();
    ArrayList<Gyroscope> gyroscopeQueue = new ArrayList<>();
    //nodeList 保存了这次触摸事件的所有x,y,pressure等的信息列表
    private NodeList nodeList;
    public String appName;
    private Boolean isAdmin = true;
    private String dir;

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
        String line = setLine();
        Log.i(TAG, line);
        Log.i(TAG, dir + "/" + appName);
        File file = FileUtils.createFile(dir, appName);
        FileUtils.writeTxt(file, line);

        //保存轨迹数据到文本文件中
        saveTrack();
        //保存轨迹信息到数据库中
        saveInDataBase();
    }

    private void saveInDataBase() {
        MyPointList data = nodeList.getMyPointList();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String temp = objectMapper.writeValueAsString(data);
            SgTrace sgTrace = new SgTrace(appName, temp, nodeList.getBeginStamp(), nodeList.getEndStamp());
            sgTrace.save();
        } catch (JsonProcessingException e) {
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }

    }

    public boolean judge() {
        ////TODO
        return true;
    }


}
