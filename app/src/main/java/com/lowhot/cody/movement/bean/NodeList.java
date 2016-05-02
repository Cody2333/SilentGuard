package com.lowhot.cody.movement.bean;

import com.lowhot.cody.movement.utils.Constants;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by cody_local on 2016/3/11.
 */

/**
 * 一次触摸事件：点击、滑动等
 */
public class NodeList {
    //delay time for sensor data
    @JsonIgnore
    public static final int DELAY = 30;

    //开始时间
    private long beginStamp;
    //结束时间
    private long endStamp;
    //轨迹点
    private MyPointList myPointList;
    //加速器
    private List<Accelerator> acceleratorQueue;
    //传感器
    private List<Gyroscope> gyroscopeQueue;

    public NodeList() {
        this.beginStamp = 0;
        this.endStamp = 0;
        myPointList = new MyPointList();

        acceleratorQueue = new ArrayList<>();
        gyroscopeQueue = new ArrayList<>();

    }


    @JsonIgnore
    public void handleSensor(LinkedBlockingQueue<Accelerator> acceleratorQueue,
                             LinkedBlockingQueue<Gyroscope> gyroscopeQueue) {
        long beginTimestamp = getBeginStamp() - DELAY;
        long endTimestamp = getEndStamp();

        Accelerator av;
        while ((av = acceleratorQueue.poll()) != null
                && av.getTimestamp() < beginTimestamp) {
        }
        if (av != null)
            this.acceleratorQueue.add(av);
        while ((av = acceleratorQueue.poll()) != null
                && av.getTimestamp() < endTimestamp) {
            this.acceleratorQueue.add(av);
        }
        // add 该时间段内所有陀螺仪值
        Gyroscope gv;
        while ((gv = gyroscopeQueue.poll()) != null
                && gv.getTimestamp() < beginTimestamp) {
        }
        if (gv != null)
            this.gyroscopeQueue.add(gv);
        while ((gv = gyroscopeQueue.poll()) != null
                && gv.getTimestamp() < endTimestamp) {
            this.gyroscopeQueue.add(gv);
        }

    }

    @JsonIgnore
    public double getAverageAccelerator() {
        double temp = 0;
        for (Accelerator sv : acceleratorQueue) {
            temp += sv.getAcce();
        }
        return temp/acceleratorQueue.size();
    }

    @JsonIgnore
    public double getAverageGyroscope() {
        double temp = 0;
        for (Gyroscope sv : gyroscopeQueue) {
            temp += sv.getGyroscope();
        }
        return temp/gyroscopeQueue.size();
    }

    @JsonIgnore
    public void addX(int x) {
        myPointList.addX(x);
    }

    @JsonIgnore
    public void addY(int y) {
        myPointList.addY(y);
    }
    @JsonIgnore
    public void addPressure(int pressure) {
        myPointList.addPressure(pressure);
    }

    @JsonIgnore
    public long getDuringTime() {
        return endStamp - beginStamp;
    }

    @JsonIgnore
    public double getAverageX() {
        return myPointList.getAverageX();
    }

    @JsonIgnore
    public double getAverageY() {
        return myPointList.getAverageY();
    }

    @JsonIgnore
    public double getAveragePressure() {
        return myPointList.getAveragePressure();
    }

    @JsonIgnore
    public List<MyPoint> getMyPoints() {
        return myPointList.getMyPoints();
    }

    @JsonIgnore
    public int getLength() {
        return myPointList.getLength();
    }

    @JsonIgnore
    public String getType(){
        if (getLength() <= 0) {
            return Constants.TYPE_UNKNOW;
        } else if (getLength() <= 2 && getLength() > 0) {
            return Constants.TYPE_CLICK;
        } else if (getLength() > 2) {
            return Constants.TYPE_SLIDE;
        }
        return Constants.TYPE_UNKNOW;
    }

    @JsonIgnore
    public String getMethod(){
        ////TODO
        return Constants.METHOD_UNKNOW;
    }
    @JsonIgnore
    public void reset() {
        myPointList.reset();
        this.beginStamp = 0;
        this.endStamp = 0;
        acceleratorQueue.clear();
        gyroscopeQueue.clear();
    }

    @JsonIgnore
    public List<double[]> getCoordinateList(){
        return myPointList.getXYList();
    }


    public void setBeginStamp(long beginStamp) {
        this.beginStamp = beginStamp;
    }

    public void setEndStamp(long endStamp) {
        this.endStamp = endStamp;
    }

    public long getBeginStamp() {
        return beginStamp;
    }

    public long getEndStamp() {
        return endStamp;
    }


    public MyPointList getMyPointList() {
        return myPointList;
    }

    public static int getDELAY() {
        return DELAY;
    }

    public void setMyPointList(MyPointList myPointList) {
        this.myPointList = myPointList;
    }

    public List<Accelerator> getAcceleratorQueue() {
        return acceleratorQueue;
    }

    public void setAcceleratorQueue(List<Accelerator> acceleratorQueue) {
        this.acceleratorQueue = acceleratorQueue;
    }

    public List<Gyroscope> getGyroscopeQueue() {
        return gyroscopeQueue;
    }

    public void setGyroscopeQueue(List<Gyroscope> gyroscopeQueue) {
        this.gyroscopeQueue = gyroscopeQueue;
    }
}
