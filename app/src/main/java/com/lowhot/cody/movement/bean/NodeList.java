package com.lowhot.cody.movement.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cody_local on 2016/3/11.
 * 保存一次触摸事件的坐标等数据
 */
public class NodeList {
    private long beginStamp;    //开始时间
    private long endStamp;      //结束时间
    private ArrayList<Integer> xList;   //x坐标
    private ArrayList<Integer> yList;   //y坐标
    private ArrayList<Integer> pressureList;    //压力

    public NodeList() {
        this.beginStamp = 0;
        this.endStamp = 0;
        this.xList = new ArrayList<>();
        this.yList = new ArrayList<>();
        this.pressureList = new ArrayList<>();
    }

    public void addX(int x){
        xList.add(x);
    }
    public void addY(int y ){
        yList.add(y);
    }
    public void addPressure(int pressure){
        pressureList.add(pressure);
    }
    public void reset(){
        xList.clear();
        yList.clear();
        pressureList.clear();
        this.beginStamp = 0;
        this.endStamp = 0;
    }

    public void setBeginStamp(long beginStamp) {
        this.beginStamp = beginStamp;
    }

    public void setEndStamp(long endStamp) {
        this.endStamp = endStamp;
    }
    public Boolean check(){
        if (xList.size() == yList.size() && xList.size() == pressureList.size() && xList.size()!=0){
            return true;
        }
        return false;
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
    public ArrayList<Integer> getxList() {
        return xList;
    }

    public ArrayList<Integer> getyList() {
        return yList;
    }

    public ArrayList<Integer> getPressureList() {
        return pressureList;
    }

    public double getAverageX() {
        Integer sumX = 0 ;
        for(Integer x : xList){
            sumX =sumX+ x;
        }
        int averageX = sumX/xList.size();
        return averageX;
    }

    public double getAverageY() {
        Integer sumY = 0 ;
        for(Integer y : yList){
            sumY =sumY+ y;
        }
        int averageY = sumY/yList.size();
        return averageY;
    }

    public double getAveragePressure() {
        Integer sumPressure = 0 ;
        for(Integer p : pressureList){
            sumPressure =sumPressure+ p;
        }
        int ave = sumPressure/pressureList.size();
        return ave;
    }
}
