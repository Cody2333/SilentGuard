package com.lowhot.cody.movement.model;

import com.lowhot.cody.movement.bean.Accelerator;
import com.lowhot.cody.movement.bean.Gyroscope;
import com.lowhot.cody.movement.bean.NodeList;
import com.lowhot.cody.movement.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class ScreenEvent {
    ArrayList<Accelerator> acceleratorQueue = new ArrayList<>();
    ArrayList<Gyroscope> gyroscopeQueue = new ArrayList<>();
    private NodeList nodeList;
    public String appName;
    private Boolean isAdmin = true;

    public ScreenEvent(NodeList nodeList,
                       LinkedBlockingQueue<Accelerator> acceleratorQueue,
                       LinkedBlockingQueue<Gyroscope> gyroscopeQueue, String appName) {
        this.nodeList = nodeList;
        this.appName = appName;
        // 选取该时间段内的 sensors

        long beginTimestamp = nodeList.getBeginStamp() - 30;
        long endTimestamp = nodeList.getEndStamp();
        // add 该时间段内所有的加速器值

        Accelerator av;
        while ((av = acceleratorQueue.poll()) != null
                && av.getTimestamp() < beginTimestamp) {
        }
        if (av != null)
            this.acceleratorQueue.add(av);
        while ((av = acceleratorQueue.poll()) != null
                && av.getTimestamp() < endTimestamp) {
            this.acceleratorQueue.add(av);
        }
        // add 该时间段内所有陀螺仪值
        Gyroscope gv;
        while ((gv = gyroscopeQueue.poll()) != null
                && gv.getTimestamp() < beginTimestamp) {
        }
        if (gv != null)
            this.gyroscopeQueue.add(gv);
        while ((gv = gyroscopeQueue.poll()) != null
                && gv.getTimestamp() < endTimestamp) {
            this.gyroscopeQueue.add(gv);
        }
    }


    public long getTime() {
        return nodeList.getDuringTime();
    }

    public double getAverageAccelerator() {
        double temp = 0;
        for (Accelerator sv : acceleratorQueue) {
            temp += sv.getAcce() * sv.getAcce();
        }
        return Math.sqrt(temp);
    }

    public double getAverageGyroscope() {
        double temp = 0;
        for (Gyroscope sv : gyroscopeQueue) {
            temp += sv.getGyroscope() * sv.getGyroscope();
        }
        return Math.sqrt(temp);
    }


    public String setLine() {

        String line = Utils.formatLine(isAdmin, nodeList.getAverageX(), nodeList.getAverageY(), nodeList.getAveragePressure(),
                nodeList.getDuringTime(), getAverageAccelerator(), getAverageGyroscope(), appName);
        return line;
    }

    public void save() throws IOException {
        String line = setLine();
        File file = Utils.createFile(appName);
        Utils.writeTxt(file, line);
    }

    public boolean judge() {
        //TODO
        return true;
    }


}
