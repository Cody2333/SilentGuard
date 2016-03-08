package com.lowhot.cody.movement.uitls;

/**
 * Created by cody_local on 2016/3/8.
 */
public class AcceleratorBean {
    private double acce;
    private long timestamp;
    private double x;
    private double y;
    private double z;

    public AcceleratorBean(double x, double y, double z, long timestamp) {
        this.timestamp = timestamp;
        this.x = x;
        this.y = y;
        this.z = z;
        this.acce = Math.sqrt(x*x+y*y+z*z);
    }


    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getAcce() {
        return acce;
    }

    public void setY(double y) {
        this.y = y;

    }

    public void setZ(double z) {
        this.z = z;
    }
}
