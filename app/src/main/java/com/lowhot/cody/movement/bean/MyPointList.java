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
        }
    }
    public void addY(int y){
        if (temp.getY()==-1){
            temp.setY(y);
        }
        if(isComplete()){
            myPoints.add(temp);
        }
    }
    public void addPressure(int p ){
        if(temp.getPressure()==-1){
            temp.setPressure(p);
        }
        if(isComplete()){
            myPoints.add(temp);
        }
    }
    public List<Integer> getxList(){
        List<Integer> xList = new ArrayList<>();
        for (MyPoint point : myPoints){
            xList.add(point.getX());
        }
        return xList;
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
}
