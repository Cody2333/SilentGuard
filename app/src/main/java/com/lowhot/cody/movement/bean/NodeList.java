package com.lowhot.cody.movement.bean;

import java.util.List;

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

    public void addX(int x){
        myPoints.addX(x);
    }
    public void addY(int y ){
        myPoints.addY(y);
    }
    public void addPressure(int pressure){
        myPoints.addPressure(pressure);
    }
    public void reset(){
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

    public long getDuringTime(){
        return endStamp-beginStamp;
    }
    public List<Integer> getxList() {
        return myPoints.getxList();
    }

    public List<Integer> getyList() {
        return myPoints.getyList();
    }

    public List<Integer> getPressureList() {
        return myPoints.getpList();
    }

    public double getAverageX() {
        List<Integer> xList = getxList();
        Integer sumX = 0 ;
        for(Integer x : xList){
            sumX =sumX+ x;
        }
        int averageX = sumX/xList.size();
        return averageX;
    }

    public double getAverageY() {
        List<Integer> yList = getyList();
        Integer sumY = 0 ;
        for(Integer y : yList){
            sumY =sumY+ y;
        }
        int averageY = sumY/yList.size();
        return averageY;
    }

    public double getAveragePressure() {
        List<Integer> pressureList = getPressureList();
        Integer sumPressure = 0 ;
        for(Integer p : pressureList){
            sumPressure =sumPressure+ p;
        }
        int ave = sumPressure/pressureList.size();
        return ave;
    }
    
    public int getlength(){
        return getxList().size();
    }
}
