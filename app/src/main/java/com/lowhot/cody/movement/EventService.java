package com.lowhot.cody.movement;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.lowhot.cody.movement.uitls.AcceleratorBean;
import com.lowhot.cody.movement.uitls.AlertDialogUtils;
import com.lowhot.cody.movement.uitls.GyroscopeBean;
import com.lowhot.cody.movement.uitls.Node;
import com.lowhot.cody.movement.uitls.ScreenEvent;
import com.lowhot.cody.movement.uitls.SensorHandler;
import com.lowhot.cody.movement.uitls.Utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressLint("ShowToast")
public class EventService extends Service implements SensorEventListener {
    private static final String TAG = "EventService";

    private SensorManager sm;
    Events events = new Events();
    ArrayList<Node> nodes = new ArrayList<Node>();
    volatile boolean FLAG_SAVING_SCREEN_EVENT = false;

    private SensorHandler sensorHandler;
    private AlertDialogUtils alertDialogUtils;
    @Override
    public void onCreate() {
        super.onCreate();
        sensorHandler = new SensorHandler();
        alertDialogUtils = new AlertDialogUtils(getApplicationContext());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public void onStart(Intent intent, int startId) {
        Log.i(TAG, "ExampleService-onStart");
        int res = events.Init();
        // debug results
        Log.d(TAG, "Event files:" + res);
        Events.intEnableDebug(1);
        // 监听键盘
        StartEventMonitor();
        // 监听传感器
        initSensor();
    }

    public void initSensor(){
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 注册加速度传感器
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        // 注册陀螺仪传感器
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
    }



    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorHandler.addAccleratorData(event.values[0],event.values[1],event.values[2],FLAG_SAVING_SCREEN_EVENT);
                break;
            case Sensor.TYPE_GYROSCOPE:
                sensorHandler.addGyroscopeData(event.values[0], event.values[1], event.values[2], FLAG_SAVING_SCREEN_EVENT);
                break;
            default:
                break;
        }

    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void StartEventMonitor() {
        Toast.makeText(getApplicationContext(), "serviceStart",
                Toast.LENGTH_SHORT).show();

        // 打开所有设备
        for (Events.InputDevice idev : events.m_Devs) {
            if (idev.Open(true)) {
                // Toast.makeText(getApplicationContext(),
                // "Device opened successfully!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(),
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
                                        + Utils.getCurrentActivityName(getApplication());
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
                                            Utils.getCurrentActivityName(getApplicationContext()));
                                    FLAG_SAVING_SCREEN_EVENT = false; // 将传感器值写入生成队列
                                    nodes.clear();
                                    if (screenEvent.judge()) {
                                        try {
                                            screenEvent.save(outFile); // 保存主人数据到文件
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
 /*                                       //下方代码为测试消息框跳出
                                        count++;
                                        if (count > 8) {  //连续有多少个手势被判断为非主人的数据
                                            //screenEvent.alert(getApplicationContext());
                                            privilege = GUEST;
                                            count = 0;
                                        }

                                    } else {
                                        count++;
                                        if (count > 8) {  //连续有多少个手势被判断为非主人的数据
                                            //screenEvent.alert(getApplicationContext());
                                            privilege = GUEST;
                                            count = 0;
                                        }
                                    }*/
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        // return myBinder;
        return null;
    }

    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }



}
