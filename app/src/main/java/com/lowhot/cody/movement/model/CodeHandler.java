package com.lowhot.cody.movement.model;

import android.util.Log;

import com.lowhot.cody.movement.bean.NodeList;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.FileUtils;

/**
 * Created by cody_local on 2016/3/9.
 */
public class CodeHandler {
    NodeList nodeList;
    public static final String TAG ="CodeHandler";
    public CodeHandler() {
        this.nodeList = new NodeList();
    }

    /**
     * 如果事件结束即 1 330 0 返回 true
     * 否则返回false
     *
     * @return
     */

    public static int CLICK = 10;
    public static int SLIDING = 11;
    public static int UNKNOW = 12;

    int style;
    public Boolean handle(int type, int code, int value) {

        if (type == Events.EV_KEY && code == Events.BTN_TOUCH && value == 1) {
            nodeList.setBeginStamp(FileUtils.getTimestamp());
        } else if (type == Events.EV_ABS && code == Events.PRESSURE) {
            nodeList.addPressure(value);
        } else if (type == Events.EV_ABS && code == Events.ABS_X) {
            nodeList.addX(value);
        } else if (type == Events.EV_ABS && code == Events.ABS_Y) {
            nodeList.addY(value);
        } else if (type == Events.EV_KEY && code == Events.BTN_TOUCH && value == 0) {
            nodeList.setEndStamp(FileUtils.getTimestamp());
            if(getLength()<=0){
                setUNKNOW();
            } else if (getLength()<=2 && getLength()>0){
                setCLICK();
            }else if(getLength()>2){
                setSLIDING();
            }
            Log.i(TAG,String.valueOf(style));
            return true;
        }
        return false;

    }

    public int getLength(){
        return nodeList.getlength();
    }
    public NodeList getNodeList() {
        return nodeList;
    }

    public void reset() {
        nodeList.reset();
    }

    public void setCLICK(){
        style = CLICK;
    }
    public void setSLIDING(){
        style = SLIDING;
    }
    public void setUNKNOW(){
        style = UNKNOW;
    }

    public int getInputStyle(){
        return style;
    }

}
