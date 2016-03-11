package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.bean.Node;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.Utils;

import java.util.ArrayList;

/**
 * Created by cody_local on 2016/3/9.
 */
public class CodeHandler {
    private ArrayList<Node> nodes;

    boolean hasPressure;
    Node temp;

    public CodeHandler() {
        this.nodes = new ArrayList<>();
        this.hasPressure = false;
        this.temp = new Node();
    }

    /**
     * 如果事件结束即 1 330 0 返回 true
     * 否则返回false
     *
     * @return
     */
    public Boolean handle(int type, int code, int value) {
        if (type == Events.EV_ABS && code == Events.PRESSURE) {
            //touch begin
            temp = new Node();
            hasPressure = true;
            temp.pressure = value;
        } else if (type == Events.EV_ABS && code == Events.ABS_X) {
            // Coordinate X
            if (!hasPressure)
                temp = new Node();
            temp.beginTimestamp = Utils.getTimestamp();
            temp.x = value;
        } else if (type == Events.EV_ABS && code == Events.ABS_Y) {
            // Coordinate Y
            temp.y = value;
            if (temp.x != 0 && temp.y != 0 && temp.beginTimestamp != 0) {
                nodes.add(temp);
            }
            hasPressure = false;
        } else if (type == 1 && code == 330 && value == 0) {
            if (nodes.size() == 0) {
                return false;
            }
            nodes.get(nodes.size() - 1).endTimestamp = Utils.getTimestamp();
            return true;
        }
        return false;

    }

    public ArrayList<Node> getNodes() {
        return nodes;
    }


    public void clearNodes() {
        this.nodes.clear();
    }
}
