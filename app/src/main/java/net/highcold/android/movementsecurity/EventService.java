package net.highcold.android.movementsecurity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import net.highcold.android.movementsecurity.Events.InputDevice;
import net.highcold.android.utils.AcceleratorValue;
import net.highcold.android.utils.GyroscopeValue;
import net.highcold.android.utils.Node;
import net.highcold.android.utils.ScreenEvent;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
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
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

@SuppressLint("ShowToast")
public class EventService extends Service implements SensorEventListener {
    private static final String TAG = "EventService";
    private IBinder myBinder = new MyBinder();
    private SensorManager sm;
    Events events = new Events();
    int count = 0, cflag = 0;
    Builder builder;
    Dialog dialog;
    Intent i;
    //
    ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<Node> copyOfNodes = new ArrayList<Node>();
    // 保存加速器和陀螺仪值的队列
    LinkedBlockingQueue<AcceleratorValue> accelerator = new LinkedBlockingQueue<AcceleratorValue>();
    LinkedBlockingQueue<GyroscopeValue> gyroscope = new LinkedBlockingQueue<GyroscopeValue>();
    boolean flag = false;
    // 当屏幕事件保存时，加速器值保存至下面缓冲队列
    LinkedBlockingQueue<AcceleratorValue> bufferQueue2 = new LinkedBlockingQueue<AcceleratorValue>();
    LinkedBlockingQueue<GyroscopeValue> bufferQueue3 = new LinkedBlockingQueue<GyroscopeValue>();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        // return myBinder;
        return null;
    }

    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        i = new Intent(getBaseContext(), MainActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    public void onDestory() {
        super.onDestroy();
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
        sm = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        // 注册加速度传感器
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME);
        // 注册陀螺仪传感器
        sm.registerListener(this, sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public class MyBinder extends Binder {
        EventService getService() {
            return EventService.this;
        }
    }

    public void StartEventMonitor() {
        Toast.makeText(getApplicationContext(), "serviceStart",
                Toast.LENGTH_SHORT).show();

        // 打开所有设备
        for (InputDevice idev : events.m_Devs) {
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
                        for (InputDevice idev : events.m_Devs) {
                            // Open more devices to see their messages
                            if (idev.getOpen() && (0 == idev.getPollingEvent())) {

                                long currentTimeMillis = System
                                        .currentTimeMillis(); // 当前时间戳

                                type = idev.getSuccessfulPollingType();
                                code = idev.getSuccessfulPollingCode();
                                value = idev.getSuccessfulPollingValue();

                                if ((type == 3 && code == 53)) {
                                    for (InputDevice allDev : events.m_Devs) {
                                        if (allDev != idev) {
                                            allDev.Close();
                                            events.m_Devs.remove(allDev); // 关掉其它设备，仅保留屏幕设备
                                        }
                                    }
                                }

                                final String line = idev.getName() + ":"
                                        + idev.getSuccessfulPollingType() + " "
                                        + idev.getSuccessfulPollingCode() + " "
                                        + idev.getSuccessfulPollingValue()
                                        + " timestamp:" + currentTimeMillis
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
                                    temp = new Node();
                                    hasPressure = true;
                                    temp.pressure = value;
                                    break;
                                } else if (type == 3 && code == 53) {
                                    if (!hasPressure)
                                        temp = new Node();
                                    temp.beginTimestamp = currentTimeMillis;
                                    temp.x = value;
                                    break;
                                } else if (type == 3 && code == 54) {
                                    temp.y = value;
                                    // temp.endTimestamp =
                                    // currentTimeMillis;
                                    if (temp.x != 0 && temp.y != 0
                                            && temp.beginTimestamp != 0)
                                        nodes.add(temp);
                                    hasPressure = false;
                                    break;
                                } else if (type == 1 && code == 330
                                        && value == 0) { // 时间结束
                                    nodes.get(nodes.size() - 1).endTimestamp = currentTimeMillis;
                                    flag = true; // 让传感器值写入缓冲队列
                                    ScreenEvent screenEvent = new ScreenEvent(
                                            nodes, accelerator, gyroscope,
                                            getCurrentActivityName());
                                    flag = false; // 将传感器值写入生成队列
                                    nodes.clear();
                                    if (screenEvent.judge()) {
                                        screenEvent.save(outFile); // 保存主人数据到文件
                                        //下方代码为测试消息框跳出
                                        count++;
                                        if (count > 8) {  //连续有多少个手势被判断为非主人的数据
                                            //screenEvent.alert(getApplicationContext());
                                            cflag = 1;
                                            count = 0;
                                        }

                                    } else {
                                        count++;
                                        if (count > 8) {  //连续有多少个手势被判断为非主人的数据
                                            //screenEvent.alert(getApplicationContext());
                                            cflag = 1;
                                            count = 0;
                                        }
                                    }
                                }

                            }

                        }
                    } catch (Exception e) {
                        System.out.println("screen event");
                    }
                }
            }
        });
        b.start();
    }

    /**
     * 获取 当前的activity名称
     */
    private String getCurrentActivityName() {
        ActivityManager am = (ActivityManager) getSystemService(Activity.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        return cn.getClassName();
    }


    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        File outFile = new File("/sdcard/eventInjectSensor.txt");
        if (cflag == 1) {
            showBox();
            cflag = 0;
        }
        if (!outFile.exists()) {
            try {
                Log.d(TAG, "create file");
                outFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        long currentTimeMillis = System.currentTimeMillis(); // 当前时间戳
        String line = "";
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                line = "Accelerometer: " + event.values[0] + ", " + event.values[1]
                        + ", " + event.values[2] + " timestamp:"
                        + currentTimeMillis;

                double acce = event.values[0] * event.values[0] + event.values[1]
                        * event.values[1] + event.values[2] * event.values[2];
                acce = Math.sqrt(acce);
                AcceleratorValue acceleratorValue = new AcceleratorValue(acce,
                        currentTimeMillis);
                if (flag) {
                    bufferQueue2.offer(acceleratorValue);
                } else {

                    try {
                        if (!bufferQueue2.isEmpty()) {
                            accelerator.addAll(bufferQueue2);
                            bufferQueue2.clear();
                        }
                        accelerator.put(acceleratorValue); // 将值添加至队列中
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                break;
            case Sensor.TYPE_GYROSCOPE:
                line = "Gyroscope: " + event.values[0] + ", " + event.values[1]
                        + ", " + event.values[2] + " timestamp:"
                        + currentTimeMillis;
                double gyro = event.values[0] * event.values[0] + event.values[1]
                        * event.values[1] + event.values[2] * event.values[2];
                gyro = Math.sqrt(gyro);
                GyroscopeValue gyroscopeValue = new GyroscopeValue(gyro,
                        currentTimeMillis);
                if (flag) { // 正在保存 ScreenEvent 事件
                    bufferQueue3.offer(gyroscopeValue); // 传感器值写入缓冲队列
                } else {
                    try {
                        if (!bufferQueue3.isEmpty()) { // 缓冲队列不为空
                            gyroscope.addAll(bufferQueue3); // 添加到生成队列
                            bufferQueue3.clear(); // 情况缓冲队列
                        }
                        gyroscope.put(gyroscopeValue); // 将传感器值写入生成队列
                    } catch (InterruptedException e1) {
                        // TODO Auto-generated catch block
                        System.out.print("bufferQueue");
                        e1.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }

        try {
            // Log.d("data", line);
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                    outFile, true));
            bufferedWriter.write(line);
            bufferedWriter.newLine();
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    private void showBox() {
        dialog.show();
    }

}
