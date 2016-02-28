package net.highcold.android.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;

public class ScreenEvent {
	public ArrayList<Node> nodes = new ArrayList<Node>();
	public ArrayList<AcceleratorValue> acceleratorValues = new ArrayList<AcceleratorValue>(); // 加速器值保存队列
	public ArrayList<GyroscopeValue> gyroscopeValues = new ArrayList<GyroscopeValue>(); // 陀螺仪值保存队列
	public String appName;

	private String line = ""; // 保存的数据

	public ScreenEvent(ArrayList<Node> nodes,
			LinkedBlockingQueue<AcceleratorValue> acceleratorValues,
			LinkedBlockingQueue<GyroscopeValue> gyroscopeValues, String appName) {
		this.nodes.addAll(nodes);
		this.appName = appName;
		// 选取该时间段内的 sensors
		
		long beginTimestamp = getFlingBeginTimestamp() - 30;
		long endTimestamp = getFlingEndTimestamp();
		// add 该时间段内所有的加速器值

		AcceleratorValue av;
		while ((av = acceleratorValues.poll()) != null
				&& av.timestamp < beginTimestamp) {
		}
		if (av != null)
			this.acceleratorValues.add(av);
		while ((av = acceleratorValues.poll()) != null
				&& av.timestamp < endTimestamp) {
			this.acceleratorValues.add(av);
		}
		// add 该时间段内所有陀螺仪值
		GyroscopeValue gv;
		while ((gv = gyroscopeValues.poll()) != null
				&& gv.timestamp < beginTimestamp) {
		}
		if (gv != null)
			this.gyroscopeValues.add(gv);
		while ((gv = gyroscopeValues.poll()) != null
				&& gv.timestamp < endTimestamp) {
			this.gyroscopeValues.add(gv);
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
		for (AcceleratorValue sv : acceleratorValues) {
			temp += sv.accelerator * sv.accelerator;
		}
		return Math.sqrt(temp);
	}

	public double getAverageGyroscope() {
		double temp = 0;
		for (GyroscopeValue sv : gyroscopeValues) {
			temp += sv.gyroscope * sv.gyroscope;
		}
		return Math.sqrt(temp);
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
		line = "x:" + x + " y:" + y + " pressure:" + pressure + " time:"
				+ getTime() + " begintime:" + getFlingBeginTimestamp()
				+ " endtime:" + getFlingEndTimestamp() + " accelerator:"
				+ getAverageAccelerator() + " gyroscope:"
				+ getAverageGyroscope() + " appName:" + appName;
		/*
		line += "\r\n";
		line += nodes.size() + " ";
		for (Node node : nodes) {
			line += node.beginTimestamp + " ";
		}
		line += "\r\n";*/
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
	
	public boolean judge(){
		
		return true;
	}
	
	/*public void alert(Context context){

		Builder builder=new Builder(context);
		builder.setTitle("Alert");
		builder.setMessage("You are not the owner!");
		builder.setNegativeButton("OK", null);
		Dialog dialog=builder.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}*/

}
