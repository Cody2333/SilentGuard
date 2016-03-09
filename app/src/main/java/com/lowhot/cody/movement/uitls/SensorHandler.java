package com.lowhot.cody.movement.uitls;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cody_local on 2016/3/9.
 * 处理传感器相关事务
 */
public class SensorHandler {

    private LinkedBlockingQueue<AcceleratorBean> acceleratorQueue = new LinkedBlockingQueue<AcceleratorBean>();
    private LinkedBlockingQueue<GyroscopeBean> gyroscopeQueue = new LinkedBlockingQueue<GyroscopeBean>();

    // 当屏幕事件保存时，加速器值保存至下面缓冲队列
    private LinkedBlockingQueue<AcceleratorBean> acceBuffer = new LinkedBlockingQueue<AcceleratorBean>();
    private LinkedBlockingQueue<GyroscopeBean> gyroBuffer = new LinkedBlockingQueue<GyroscopeBean>();

    public SensorHandler() {

        this.acceleratorQueue = new LinkedBlockingQueue<AcceleratorBean>();
        this.gyroscopeQueue = new LinkedBlockingQueue<GyroscopeBean>();

        // 当屏幕事件保存时，加速器值保存至下面缓冲队列
        this.acceBuffer = new LinkedBlockingQueue<AcceleratorBean>();
        this.gyroBuffer = new LinkedBlockingQueue<GyroscopeBean>();
    }

    public void addGyroscopeData(double x,double y,double z,Boolean isReading){
        GyroscopeBean gyroscopeBean = new GyroscopeBean(x,y,z,Utils.getTimestamp());
        if (isReading) { // 正在保存 ScreenEvent 事件
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
    }

    public void addAccleratorData(double x,double y,double z,Boolean isReading){
        AcceleratorBean acceleratorBean = new AcceleratorBean(x,y,z,Utils.getTimestamp());
        if (isReading) {
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
    }

    public LinkedBlockingQueue<AcceleratorBean> getAcceleratorQueue() {
        return acceleratorQueue;
    }

    public void setAcceleratorQueue(LinkedBlockingQueue<AcceleratorBean> acceleratorQueue) {
        this.acceleratorQueue = acceleratorQueue;
    }

    public LinkedBlockingQueue<GyroscopeBean> getGyroscopeQueue() {
        return gyroscopeQueue;
    }

    public void setGyroscopeQueue(LinkedBlockingQueue<GyroscopeBean> gyroscopeQueue) {
        this.gyroscopeQueue = gyroscopeQueue;
    }
}
