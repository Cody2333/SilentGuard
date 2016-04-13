package com.lowhot.cody.movement.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cody_local on 2016/4/13.
 */
public class MyPointList {
    private List<MyPoint> myPoints;
    private MyPoint temp;
    public MyPointList() {
        myPoints = new ArrayList<>();
        temp = new MyPoint();
    }

    public void addX(int x){
        if (temp.getX()==-1){
            temp.setX(x);
        }
        if(isComplete()){
            myPoints.add(temp);
            temp=new MyPoint();
        }
    }
    public void addY(int y){
        if (temp.getY()==-1){
            temp.setY(y);
        }
        if(isComplete()){
            myPoints.add(temp);
            temp=new MyPoint();
        }
    }
    public void addPressure(int p ){
        if(temp.getPressure()==-1){
            temp.setPressure(p);
        }
        if(isComplete()){
            myPoints.add(temp);
            temp=new MyPoint();
        }
    }
    public List<Integer> getxList(){
        List<Integer> xList = new ArrayList<>();
        for (MyPoint point : myPoints){
            xList.add(point.getX());
        }
        return xList;
    }
    public int getAverageX(){
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
        List<Integer> pressureList = getpList();
        Integer sumPressure = 0 ;
        for(Integer p : pressureList){
            sumPressure =sumPressure+ p;
        }
        int ave = sumPressure/pressureList.size();
        return ave;
    }
    public List<Integer> getyList(){
        List<Integer> yList = new ArrayList<>();
        for (MyPoint point : myPoints){
            yList.add(point.getY());
        }
        return yList;
    }
    public List<Integer> getpList(){
        List<Integer> pList = new ArrayList<>();
        for (MyPoint point : myPoints){
            pList.add(point.getPressure());
        }
        return pList;
    }
    public Boolean isComplete(){
        return temp.isCompleted();
    }

    public void reset(){
        myPoints.clear();
        temp.reset();
    }
    public int getLength(){
        return myPoints.size();
    }
}
