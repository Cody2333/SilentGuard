package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.bean.NodeList;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.FileUtils;

/**
 * 处理屏幕点击事件
 */
public class CodeHandler {
    private NodeList nodeList;

    public CodeHandler() {
        this.nodeList = new NodeList();
    }

    /**
     * 如果事件结束即 1 330 0 返回 true
     * 否则返回false
     * @return 一个点击或者滑动时间是否结束
     */
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
            //if (nodeList.check()) {
            //    return true;
            //}
            return true;
        }
        return false;

    }

    public NodeList getNodeList() {
        return nodeList;
    }

    public void reset() {
        nodeList.reset();
    }


}
