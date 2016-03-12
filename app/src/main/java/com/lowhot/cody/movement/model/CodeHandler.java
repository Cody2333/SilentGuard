package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.bean.NodeList;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.Utils;

/**
 * Created by cody_local on 2016/3/9.
 */
public class CodeHandler {
    NodeList nodeList;

    public CodeHandler() {
        this.nodeList = new NodeList();
    }

    /**
     * 如果事件结束即 1 330 0 返回 true
     * 否则返回false
     *
     * @return
     */
    public Boolean handle(int type, int code, int value) {
        if (type == Events.EV_KEY && code == Events.BTN_TOUCH && value == 1) {
            nodeList.setBeginStamp(Utils.getTimestamp());
        } else if (type == Events.EV_ABS && code == Events.PRESSURE) {
            nodeList.addPressure(value);
        } else if (type == Events.EV_ABS && code == Events.ABS_X) {
            nodeList.addX(value);
        } else if (type == Events.EV_ABS && code == Events.ABS_Y) {
            nodeList.addY(value);
        } else if (type == Events.EV_KEY && code == Events.BTN_TOUCH && value == 0) {
            nodeList.setEndStamp(Utils.getTimestamp());
            if (nodeList.check()){
                return true;

            }
        }
        return false;

    }

    public NodeList getNodeList(){
        return nodeList;
    }

    public void reset(){
        nodeList.reset();
    }


}
