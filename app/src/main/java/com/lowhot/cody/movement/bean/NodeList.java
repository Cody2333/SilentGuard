package com.lowhot.cody.movement.bean;

import java.util.List;

/**
 * Created by cody_local on 2016/3/11.
 */
public class NodeList {
    //保存一次触摸事件的x,y,pressure 等属性
    private long beginStamp;
    private long endStamp;
    private MyPointList myPointList;

    public NodeList() {
        this.beginStamp = 0;
        this.endStamp = 0;
        myPointList = new MyPointList();
    }


    public void addX(int x) {
        myPointList.addX(x);
    }

    public void addY(int y) {
        myPointList.addY(y);
    }

    public void addPressure(int pressure) {
        myPointList.addPressure(pressure);
    }

    public void reset() {
        myPointList.reset();
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
        return myPointList.getAverageX();
    }

    public double getAverageY() {
        return myPointList.getAverageY();
    }

    public double getAveragePressure() {
        return myPointList.getAveragePressure();
    }

    public List<Integer> getxList(){
        return myPointList.getxList();
    }

    public List<Integer> getyList(){
        return myPointList.getyList();
    }
    public  List<MyPoint> getMyPoints(){
        return myPointList.getMyPoints();
    }

    public int getlength() {
        return myPointList.getLength();
    }

}
