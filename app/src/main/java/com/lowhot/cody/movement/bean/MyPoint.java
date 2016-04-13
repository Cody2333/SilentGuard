package com.lowhot.cody.movement.bean;

import com.lowhot.cody.movement.utils.FileUtils;

/**
 * Created by cody_local on 2016/4/13.
 */
public class MyPoint {
    private int x = -1;
    private int y = -1;
    private int pressure = -1;
    private long xStamp = -1;
    private long yStamp = -1;
    private long pStamp = -1;

    public MyPoint() {
    }

    public MyPoint(int x, int y, int pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
        this.xStamp = FileUtils.getTimestamp();
        this.yStamp = FileUtils.getTimestamp();
        this.pStamp = FileUtils.getTimestamp();
    }

    public Boolean isCompleted() {
        if (x != -1 && y != -1 && pressure != -1) {
            return true;
        } else {
            return false;
        }
    }

    public int getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
        this.xStamp = FileUtils.getTimestamp();
    }

    public int getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
        this.yStamp = FileUtils.getTimestamp();
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
        this.pStamp = FileUtils.getTimestamp();
    }

    public void reset() {
        x = -1;
        y = -1;
        pressure = -1;
        xStamp = -1;
        yStamp = -1;
        pStamp = -1;
    }

}
