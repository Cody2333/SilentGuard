package com.lowhot.cody.movement.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Created by cody_local on 2016/3/8.
 */
public class Gyroscope {
    private double x;
    private double y;
    private double z;
    @JsonIgnore
    private double gyroscope;
    private long timestamp;

    public Gyroscope() {
    }

    public Gyroscope(double x, double y, double z, long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
        this.gyroscope = Math.sqrt(x*x+y*y+z*z);

    }

    public double getGyroscope() {
        return gyroscope;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }


    public void setZ(double z) {
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public void setGyroscope(double gyroscope) {
        this.gyroscope = gyroscope;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
