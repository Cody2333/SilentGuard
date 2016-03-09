package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.bean.Accelerator;
import com.lowhot.cody.movement.bean.Gyroscope;
import com.lowhot.cody.movement.utils.Utils;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cody_local on 2016/3/9.
 * 处理传感器相关事务
 */
public class SensorHandler {
    private static final String TAG = "SensorHandler";

    private LinkedBlockingQueue<Accelerator> acceleratorQueue = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Gyroscope> gyroscopeQueue = new LinkedBlockingQueue<>();

    // 当屏幕事件保存时，加速器值保存至下面缓冲队列
    private LinkedBlockingQueue<Accelerator> acceBuffer = new LinkedBlockingQueue<>();
    private LinkedBlockingQueue<Gyroscope> gyroBuffer = new LinkedBlockingQueue<>();

    public SensorHandler() {

        this.acceleratorQueue = new LinkedBlockingQueue<Accelerator>();
        this.gyroscopeQueue = new LinkedBlockingQueue<Gyroscope>();

        // 当屏幕事件保存时，加速器值保存至下面缓冲队列
        this.acceBuffer = new LinkedBlockingQueue<Accelerator>();
        this.gyroBuffer = new LinkedBlockingQueue<Gyroscope>();
    }

    public void addGyroscopeData(double x, double y, double z, Boolean isReading) {
        Gyroscope gyroscope = new Gyroscope(x, y, z, Utils.getTimestamp());
        if (isReading) { // 正在保存 ScreenEvent 事件
            gyroBuffer.offer(gyroscope); // 传感器值写入缓冲队列
        } else {
            try {
                if (!gyroBuffer.isEmpty()) { // 缓冲队列不为空
                    gyroscopeQueue.addAll(gyroBuffer); // 添加到生成队列
                    gyroBuffer.clear(); // 情况缓冲队列
                }
                gyroscopeQueue.put(gyroscope); // 将传感器值写入生成队列
            } catch (InterruptedException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
    }

    public void addAccleratorData(double x, double y, double z, Boolean isReading) {
        Accelerator accelerator = new Accelerator(x, y, z, Utils.getTimestamp());
        if (isReading) {
            acceBuffer.offer(accelerator);
        } else {

            try {
                if (!acceBuffer.isEmpty()) {
                    acceleratorQueue.addAll(acceBuffer);
                    acceBuffer.clear();
                }
                acceleratorQueue.put(accelerator); // 将值添加至队列中
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
    }

    public LinkedBlockingQueue<Accelerator> getAcceleratorQueue() {
        return acceleratorQueue;
    }

    public void setAcceleratorQueue(LinkedBlockingQueue<Accelerator> acceleratorQueue) {
        this.acceleratorQueue = acceleratorQueue;
    }

    public LinkedBlockingQueue<Gyroscope> getGyroscopeQueue() {
        return gyroscopeQueue;
    }

    public void setGyroscopeQueue(LinkedBlockingQueue<Gyroscope> gyroscopeQueue) {
        this.gyroscopeQueue = gyroscopeQueue;
    }
}
