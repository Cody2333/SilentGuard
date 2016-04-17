package com.lowhot.cody.movement.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.lowhot.cody.movement.utils.FileUtils;


/**
 * Created by cody_local on 2016/4/13.
 */
public class MyPoint {
    private int x = -1;
    private int y = -1;
    private int pressure = -1;
    private long timestamp = -1;
    public MyPoint() {
    }

    public MyPoint(int x, int y, int pressure) {
        this.x = x;
        this.y = y;
        this.pressure = pressure;
    }

    @JsonIgnore
    public Boolean isCompleted() {
        if (x != -1 && y != -1 && pressure != -1) {
            if (timestamp == -1) {
                //当一个MyPoint对象属性值全了的时候设置时间戳
                timestamp = FileUtils.getTimestamp();
            }
            return true;
        } else {
            return false;
        }
    }

    @JsonIgnore
    public Boolean isNew() {
        if (x == -1 && y == -1 && pressure == -1) {
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
    }

    public int getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public int getPressure() {
        return pressure;
    }

    public void setPressure(Integer pressure) {
        this.pressure = pressure;
    }

    @JsonIgnore
    public void reset() {
        x = -1;
        y = -1;
        pressure = -1;
        timestamp = -1;

    }


}
