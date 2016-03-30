package com.lowhot.cody.movement.bean;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;

/**
 * Created by cody_local on 2016/3/30.
 */
public class Config {
    int s, t, r;
    double g, n;
    String appName="default";

    public Config() {
        this.s = 2;
        this.t = 2;
        this.g = 0.00001;
        this.r = 1;
        this.n = 0.19;
        this.appName = "default";
    }

    public Config(String appName) {
        this.s = 2;
        this.t = 2;
        this.g = 0.00001;
        this.r = 1;
        this.n = 0.19;
        this.appName = appName;
    }

    public Config(String appName, int s, int t, double g, int r, double n) {
        this.s = s;
        this.t = t;
        this.r = r;
        this.g = g;
        this.n = n;
        this.appName = appName;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public int getT() {
        return t;
    }

    public void setT(int t) {
        this.t = t;
    }

    public int getR() {
        return r;
    }

    public void setR(int r) {
        this.r = r;
    }

    public double getG() {
        return g;
    }

    public void setG(double g) {
        this.g = g;
    }

    public double getN() {
        return n;
    }

    public void setN(double n) {
        this.n = n;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

}
