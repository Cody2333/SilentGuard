package com.lowhot.cody.movement.bean;

/**
 * Created by cody_local on 2016/3/11.
 */
public class NodeList {
    //保存一次触摸事件的x,y,pressure 等属性
    private long beginStamp;
    private long endStamp;
    private MyPointList myPoints;

    public NodeList() {
        this.beginStamp = 0;
        this.endStamp = 0;
        myPoints = new MyPointList();
    }


    public void addX(int x) {
        myPoints.addX(x);
    }

    public void addY(int y) {
        myPoints.addY(y);
    }

    public void addPressure(int pressure) {
        myPoints.addPressure(pressure);
    }

    public void reset() {
        myPoints.reset();
        this.beginStamp = 0;
        this.endStamp = 0;
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

    public long getDuringTime() {
        return endStamp - beginStamp;
    }

    public double getAverageX() {
        return myPoints.getAverageX();
    }

    public double getAverageY() {
        return myPoints.getAverageY();
    }

    public double getAveragePressure() {
        return myPoints.getAveragePressure();
    }

    public int getlength() {
        return myPoints.getLength();
    }
}
