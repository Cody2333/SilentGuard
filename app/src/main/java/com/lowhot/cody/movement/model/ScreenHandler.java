package com.lowhot.cody.movement.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.lowhot.cody.movement.Events;
import com.lowhot.cody.movement.bean.Node;
import com.lowhot.cody.movement.uitls.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by cody_local on 2016/3/9.
 * 处理触摸事件相关事务
 */
public class ScreenHandler {
    private static final String TAG = "ScreenHandler";
    private ArrayList<Node> nodes;
    private Events events;
    private volatile boolean FLAG_SAVING_SCREEN_EVENT;
    private Context ctx;
    private SensorHandler sensorHandler;

    public ScreenHandler(Context ctx, SensorHandler sensorHandler) {
        this.ctx = ctx;
        this.sensorHandler = sensorHandler;
        nodes = new ArrayList<>();
        events = new Events();
        FLAG_SAVING_SCREEN_EVENT = false;

        //初始化 events
        events.Init();
        Events.intEnableDebug(1);
    }

    public boolean isFLAG_SAVING_SCREEN_EVENT() {
        return FLAG_SAVING_SCREEN_EVENT;
    }

    public void setFLAG_SAVING_SCREEN_EVENT(boolean FLAG_SAVING_SCREEN_EVENT) {
        this.FLAG_SAVING_SCREEN_EVENT = FLAG_SAVING_SCREEN_EVENT;
    }

    public Events getEvents() {
        return events;
    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<Node> nodes) {
        this.nodes = nodes;
    }

    public void StartEventMonitor() {
        Toast.makeText(ctx, "serviceStart",
                Toast.LENGTH_SHORT).show();

        // 打开所有设备
        for (Events.InputDevice idev : events.m_Devs) {
            if (idev.Open(true)) {
                // Toast.makeText(getApplicationContext(),
                // "Device opened successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx,
                        "Device failed to open. Do you have root?",
                        Toast.LENGTH_SHORT).show();
            }
        }

        Thread b = new Thread(new Runnable() {

            public void run() {
                File outFile = new File("/sdcard/eventInject.txt");
                File originalScreenDataFile = new File(
                        "/sdcard/eventInjectOrignial.txt");

                if (!outFile.exists()) {
                    try {
                        Log.d(TAG, "create file");
                        outFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (!originalScreenDataFile.exists()) {
                    try {
                        Log.d(TAG, "create file");
                        originalScreenDataFile.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                int type, code, value;
                boolean hasPressure = false;
                Node temp = new Node();
                while (true) {
                    try {
                        for (Events.InputDevice idev : events.m_Devs) {
                            // Open more devices to see their messages
                            if (idev.getOpen() && (0 == idev.getPollingEvent())) {


                                type = idev.getSuccessfulPollingType();
                                code = idev.getSuccessfulPollingCode();
                                value = idev.getSuccessfulPollingValue();

                    /*)            if ((type == 3 && code == 53)) {
                                    for (Events.InputDevice allDev : events.m_Devs) {
                                        if (allDev != idev) {
                                            allDev.Close();
                                            events.m_Devs.remove(allDev); // 关掉其它设备，仅保留屏幕设备
                                        }
                                    }
                                }*/

                                final String line = idev.getName() + ":"
                                        + type + " "
                                        + code + " "
                                        + value
                                        + " timestamp:" + Utils.getTimestamp()
                                        + " appName:"
                                        + Utils.getCurrentActivityName(ctx);
                                Log.d("data", line);
                                BufferedWriter bufferedWriter = new BufferedWriter(
                                        new FileWriter(originalScreenDataFile,
                                                true));
                                bufferedWriter.write(line);
                                bufferedWriter.newLine();
                                bufferedWriter.flush();
                                bufferedWriter.close();

                                if (type == 3 && code == 58) {
                                    //touch begin
                                    temp = new Node();
                                    hasPressure = true;
                                    temp.pressure = value;
                                    break;
                                } else if (type == 3 && code == 53) {
                                    // Coordinate X
                                    if (!hasPressure)
                                        temp = new Node();
                                    temp.beginTimestamp = Utils.getTimestamp();
                                    temp.x = value;
                                    break;
                                } else if (type == 3 && code == 54) {
                                    // Coordinate Y
                                    temp.y = value;

                                    if (temp.x != 0 && temp.y != 0
                                            && temp.beginTimestamp != 0)
                                        nodes.add(temp);
                                    hasPressure = false;
                                    break;
                                } else if (type == 1 && code == 330
                                        && value == 0) { // 时间结束
                                    nodes.get(nodes.size() - 1).endTimestamp = Utils.getTimestamp();
                                    FLAG_SAVING_SCREEN_EVENT = true; // 让传感器值写入缓冲队列
                                    ScreenEvent screenEvent = new ScreenEvent(
                                            nodes, sensorHandler.getAcceleratorQueue(), sensorHandler.getGyroscopeQueue(),
                                            Utils.getCurrentActivityName(ctx));
                                    FLAG_SAVING_SCREEN_EVENT = false; // 将传感器值写入生成队列
                                    nodes.clear();
                                    if (screenEvent.judge()) {
                                        try {
                                            screenEvent.save(outFile); // 保存主人数据到文件
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        b.start();
    }

}
