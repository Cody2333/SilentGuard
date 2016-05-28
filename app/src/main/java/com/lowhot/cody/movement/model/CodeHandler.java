package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.bean.InputEvent;
import com.lowhot.cody.movement.utils.Events;
import com.lowhot.cody.movement.utils.FileUtils;

/**
 * Created by cody_local on 2016/3/9.
 */
public class CodeHandler {
    InputEvent inputEvent;
    public static final String TAG = "CodeHandler";

    public CodeHandler() {
        this.inputEvent = new InputEvent();
    }

    /**
     * 如果事件结束即 1 330 0 返回 true
     * 否则返回false
     *
     * @return
     */

    public Boolean handle(int type, int code, int value) {

        if (type == Events.EV_KEY && code == Events.BTN_TOUCH && value == 1) {
            inputEvent.setBeginStamp(FileUtils.getTimestamp());
        } else if (type == Events.EV_ABS && code == Events.PRESSURE) {
            inputEvent.addPressure(value);
        } else if (type == Events.EV_ABS && code == Events.ABS_X) {
            inputEvent.addX(value);
        } else if (type == Events.EV_ABS && code == Events.ABS_Y) {
            inputEvent.addY(value);
        } else if (type == Events.EV_KEY && code == Events.BTN_TOUCH && value == 0) {
            inputEvent.setEndStamp(FileUtils.getTimestamp());
            return true;
        }
        return false;

    }

    public InputEvent getInputEvent() {
        return inputEvent;
    }

    public void reset() {
        inputEvent.reset();
    }


}
