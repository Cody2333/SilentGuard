package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.bean.Node;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.Utils;

import java.util.ArrayList;

/**
 * Created by cody_local on 2016/3/9.
 */
public class CodeHandler {
    private int type;
    private int code;
    private int value;
    private ArrayList<Node> nodes;

    boolean hasPressure;
    Node temp;

    public CodeHandler(ArrayList<Node> nodes) {

        this.nodes = nodes;
        this.hasPressure = false;
        this.temp = new Node();
    }

    /**
     * 如果事件结束即 1 330 0 返回 true
     * 否则返回false
     * @return
     */
    public Boolean handle(int type,int code,int value) {
        if (type == 3 && code == 58) {
            //touch begin
            temp = new Node();
            hasPressure = true;
            temp.pressure = value;
        } else if (type == 3 && code == 53) {
            // Coordinate X
            if (!hasPressure)
                temp = new Node();
            temp.beginTimestamp = Utils.getTimestamp();
            temp.x = value;
        } else if (type == 3 && code == 54) {
            // Coordinate Y
            temp.y = value;
            if (temp.x != 0 && temp.y != 0
                    && temp.beginTimestamp != 0)
                nodes.add(temp);
            hasPressure = false;
        } else if (type == 1 && code == 330
                && value == 0) {
            if (nodes.size()==0) return false;
            nodes.get(nodes.size() - 1).endTimestamp = Utils.getTimestamp();
            return true;
        }
        return false;

    }
}
