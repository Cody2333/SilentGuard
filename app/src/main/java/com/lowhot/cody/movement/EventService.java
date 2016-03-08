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
import com.lowhot.cody.movement.uitls.GyroscopeBean;
import com.lowhot.cody.movement.uitls.Node;
import com.lowhot.cody.movement.uitls.ScreenEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

@SuppressLint("ShowToast")
public class EventService extends Service implements SensorEventListener {
    private static final String TAG = "EventService";

    private static final int ADMIN = 0;
    private static final int GUEST = 1;

    private SensorManager sm;
    Events events = new Events();
    int count = 0;
    int privilege = ADMIN;

    Builder builder;
    Dialog dialog;
    Intent i;
    //
    ArrayList<Node> nodes = new ArrayList<Node>();
    // 保存加速器和陀螺仪值的队列
    LinkedBlockingQueue<AcceleratorBean> acceleratorQueue = new LinkedBlockingQueue<AcceleratorBean>();
    LinkedBlockingQueue<GyroscopeBean> gyroscopeQueue = new LinkedBlockingQueue<GyroscopeBean>();

    boolean FLAG_SAVING_SENSOR_EVENT = false;
    // 当屏幕事件保存时，加速器值保存至下面缓冲队列
    LinkedBlockingQueue<AcceleratorBean> acceBuffer = new LinkedBlockingQueue<AcceleratorBean>();
    LinkedBlockingQueue<GyroscopeBean> gyroBuffer = new LinkedBlockingQueue<GyroscopeBean>();

    @Override
    public void onCreate() {
        super.onCreate();
        i = new Intent(getBaseContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        createAlertDialog();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = START_STICKY;
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

    public void createAlertDialog() {
        builder = new Builder(getApplicationContext());
        builder.setTitle("Alert");
        builder.setMessage("You are not the owner!");
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }

    public void onSensorChanged(SensorEvent event) {
        if (privilege == GUEST) {
            showBox();
            privilege = ADMIN;
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                AcceleratorBean acceleratorBean = new AcceleratorBean(event.values[0], event.values[1], event.values[2], getTimestamp());
                if (FLAG_SAVING_SENSOR_EVENT) {
                    acceBuffer.offer(acceleratorBean);
                } else {

                    try {
                        if (!acceBuffer.isEmpty()) {
                            acceleratorQueue.addAll(acceBuffer);
                            acceBuffer.clear();
                        }
                        acceleratorQueue.put(acceleratorBean); // 将值添加至队列中
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                GyroscopeBean gyroscopeBean = new GyroscopeBean(event.values[0], event.values[1], event.values[2], getTimestamp());
                if (FLAG_SAVING_SENSOR_EVENT) { // 正在保存 ScreenEvent 事件
                    gyroBuffer.offer(gyroscopeBean); // 传感器值写入缓冲队列
                } else {
                    try {
                        if (!gyroBuffer.isEmpty()) { // 缓冲队列不为空
                            gyroscopeQueue.addAll(gyroBuffer); // 添加到生成队列
                            gyroBuffer.clear(); // 情况缓冲队列
                        }
                        gyroscopeQueue.put(gyroscopeBean); // 将传感器值写入生成队列
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
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
                                        + " timestamp:" + getTimestamp()
                                        + " appName:"
                                        + getCurrentActivityName();
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
                                    temp.beginTimestamp = getTimestamp();
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
                                    nodes.get(nodes.size() - 1).endTimestamp = getTimestamp();
                                    FLAG_SAVING_SENSOR_EVENT = true; // 让传感器值写入缓冲队列
                                    ScreenEvent screenEvent = new ScreenEvent(
                                            nodes, acceleratorQueue, gyroscopeQueue,
                                            getCurrentActivityName());
                                    FLAG_SAVING_SENSOR_EVENT = false; // 将传感器值写入生成队列
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

    /**
     * 获取 当前的activity名称
     */
    private String getCurrentActivityName() {
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }

    private long getTimestamp() {
        return System.currentTimeMillis();
    }
    private void showBox() {
        dialog.show();
    }
}
