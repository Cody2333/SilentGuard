package com.lowhot.cody.movement.uitls;

import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class ScreenEvent {
    public ArrayList<Node> nodes = new ArrayList<Node>();
    ArrayList<AcceleratorBean> acceleratorQueue = new ArrayList<>();
    ArrayList<GyroscopeBean> gyroscopeQueue = new ArrayList<>();

    public String appName;

    private String line = ""; // 保存的数据
    private Boolean isAdmin = true;
    public ScreenEvent(ArrayList<Node> nodes,
                       LinkedBlockingQueue<AcceleratorBean> acceleratorQueue,
                       LinkedBlockingQueue<GyroscopeBean> gyroscopeQueue, String appName) {
        this.nodes.addAll(nodes);
        this.appName = appName;
        // 选取该时间段内的 sensors

        long beginTimestamp = getFlingBeginTimestamp() - 30;
        long endTimestamp = getFlingEndTimestamp();
        // add 该时间段内所有的加速器值

        AcceleratorBean av;
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
        GyroscopeBean gv;
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

    public long getFlingBeginTimestamp() {
        return nodes.get(0).beginTimestamp;
    }

    public long getFlingEndTimestamp() {
        return nodes.get(nodes.size() - 1).endTimestamp;
    }

    public long getTime() {
        return getFlingEndTimestamp() - getFlingBeginTimestamp();
    }

    public double getAverageAccelerator() {
        double temp = 0;
        for (AcceleratorBean sv : acceleratorQueue) {
            temp += sv.getAcce() * sv.getAcce();
        }
        return Math.sqrt(temp);
    }

    public double getAverageGyroscope() {
        double temp = 0;
        for (GyroscopeBean sv : gyroscopeQueue) {
            temp += sv.getGyroscope() * sv.getGyroscope();
        }
        return Math.sqrt(temp);
    }

    public String formatLine(Boolean isAdmin, double x, double y,double pressure, long time,double accelerator, double gyroscope,String appName){
        int isAdminInt = -1;
        if (isAdmin){
            isAdminInt = 1;
        }else{
            isAdminInt =-1;
        }
        String line = String.valueOf(isAdminInt)
                +" 0:" + x
                +" 1:" + y
                +" 2:" + pressure
                +" 3:" + time
                +" 4:" + accelerator
                +" 5:" + gyroscope
                +" 6:" + appName;
        return line;

    }
    public void setLine() {
        double x = 0, y = 0, pressure = 0;
        for (Node node : nodes) {
            x += node.x;
            y += node.y;
            pressure += node.pressure;
        }
        x /= nodes.size(); // 平均 x坐标
        y /= nodes.size(); // 平均 y坐标
        pressure /= nodes.size(); // 平均压力
        line = formatLine(isAdmin, x, y, pressure, getTime(), getAverageAccelerator(), getAverageGyroscope(), appName);
    }
    public void save(File outFile) throws IOException {
        setLine();
        Log.d("dealedData", line);
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(
                outFile, true));
        bufferedWriter.write(line);
        bufferedWriter.newLine();
        bufferedWriter.flush();
        bufferedWriter.close();
    }

    public boolean judge() {

        return true;
    }


}
