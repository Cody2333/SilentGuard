package com.lowhot.cody.movement.bean;

import java.util.ArrayList;

/**
 * Created by cody_local on 2016/3/11.
 */
public class touchData {
    //保存一次触摸事件的x,y,pressure 等属性
    private ArrayList<Node> nodes;
    private long beginStamp;
    private long enfStamp;
    private long duringTime;
    private double averageX;
    private double averageY;
    private double averagePressure;


}
