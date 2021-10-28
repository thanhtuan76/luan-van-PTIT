/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csim01;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import com.hieu.arima.*;

/**
 *
 * @author Admin
 */
public class ArimaTest {

    public static void main(String args[]) {
        Scanner ino = null;

        try {
            ArrayList<Double> arraylist = new ArrayList<Double>();
            //ino=new Scanner(new File(System.getProperty("user.dir")+"/data/ceshidata.txt"));
            ino = new Scanner(new File(System.getProperty("user.dir") + "/data/test.txt"));
            while (ino.hasNext()) {
                arraylist.add(Double.parseDouble(ino.next()));
            }
            double[] dataArray = new double[arraylist.size() - 1];
            for (int i = 0; i < arraylist.size() - 1; i++) {
                dataArray[i] = arraylist.get(i);
            }

            //System.out.println(arraylist.size());
            ARIMA arima = new ARIMA(dataArray);

            int[] model = arima.getARIMAmodel();
            System.out.println("Total observations ]=" + "[" + arraylist.size()+ "]");
            System.out.println("Best model is [p,q]=" + "[" + model[0] + " " + model[1] + "]");
            System.out.println("Predict value=" + arima.aftDeal(arima.predictValue(model[0], model[1])));
            System.out.println("Predict error=" + (arima.aftDeal(arima.predictValue(model[0], model[1])) - arraylist.get(arraylist.size() - 1)) / arraylist.get(arraylist.size() - 1) * 100 + "%");

            //	String[] str = (String[])list1.toArray(new String[0]);	
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            ino.close();
        }
    }
}
