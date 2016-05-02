package com.example.cody.movement;

/*
 * FastDtwTest.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */



import com.timeseries.TimeSeries;
import com.util.DistanceFunction;
import com.util.DistanceFunctionFactory;
import com.dtw.TimeWarpInfo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;


/**
 * This class contains a main method that executes the FastDTW algorithm on two
 * time series with a specified radius.
 *
 * @author Stan Salvador, stansalvador@hotmail.com
 * @since Jul 14, 2004
 */
public class DTWTest
{
    /**
     * This main method executes the FastDTW algorithm on two time series with a
     * specified radius. The time series arguments are file names for files that
     * contain one measurement per line (time measurements are an optional value
     * in the first column). After calculating the warp path, the warp
     * path distance will be printed to standard output, followed by the path
     * in the format "(0,0),(1,0),(2,1)..." were each pair of numbers in
     * parenthesis are indexes of the first and second time series that are
     * linked in the warp path
     *
     * @param args  command line arguments (see method comments)
     */
    public static void main(String[] args)
    {
        if (args.length!=3 && args.length!=4)
        {
            System.out.println("USAGE:  java FastDtwTest timeSeries1 timeSeries2 radius [EuclideanDistance|ManhattanDistance|BinaryDistance]");
            System.exit(1);
        }
        else
        {
            final TimeSeries tsI0 = new TimeSeries(args[0], false, false, ',');
            final TimeSeries tsJ0 = new TimeSeries(args[1], false, false, ',');


            ArrayList trace1 = new ArrayList();
            ArrayList trace2 = new ArrayList();
            double[] tmp;
            try {
                BufferedReader br1 = new BufferedReader (new FileReader(args[0]));
                String line;
                while ((line = br1.readLine()) != null){
                    tmp = new double[]{Double.parseDouble(line)};
                    trace1.add(tmp);
                }
                br1.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                BufferedReader br1 = new BufferedReader (new FileReader(args[1]));
                String line;
                while ((line = br1.readLine()) != null){
                    tmp = new double[]{Double.parseDouble(line)};
                    trace2.add(tmp);
                }
                br1.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final TimeSeries tsI = new TimeSeries(trace1);
            final TimeSeries tsJ = new TimeSeries(trace2);
            final DistanceFunction distFn;
            if (args.length < 4)
            {
                distFn = DistanceFunctionFactory.getDistFnByName("EuclideanDistance");
            }
            else
            {
                distFn = DistanceFunctionFactory.getDistFnByName(args[3]);
            }   // end if
            long t1 = System.currentTimeMillis();
            final TimeWarpInfo info = com.dtw.FastDTW.getWarpInfoBetween(tsI, tsJ, Integer.parseInt(args[2]), distFn);
            long t2 = System.currentTimeMillis();
            System.out.println("Warp Distance: " + info.getDistance());
            System.out.println("Warp Path:     " + info.getPath());
            System.out.println("time:     " + (t2-t1));
        }  // end if

    }  // end main()


}  // end class FastDtwTest
