/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim;

import com.hieu.arima.ARIMA;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Admin
 */
public class VmExt extends Vm {

    private List<Double> LastRT;
    private double PredictedRT;

    public VmExt(
            int id,
            int userId,
            double mips,
            int numberOfPes,
            int ram,
            long bw,
            long size,
            String vmm,
            CloudletScheduler cloudletScheduler) {
        super(id, userId, mips, numberOfPes, ram, bw, size, vmm, cloudletScheduler);
        LastRT = new ArrayList<Double>();
    }

    public double getPredictedRT() {
        List<Double> arraylist = getLastRT();
        if(arraylist.size()>15){
        double[] dataArray = new double[arraylist.size() - 1];
        for (int i = 0; i < arraylist.size() - 1; i++) {
            dataArray[i] = arraylist.get(i);
        }
        ARIMA arima = new ARIMA(dataArray);
        int[] model = arima.getARIMAmodel();
        this.PredictedRT = arima.aftDeal(arima.predictValue(model[0], model[1]));
        }
        else
        {
            this.PredictedRT =arraylist.get(arraylist.size()-1);
        }
        return this.PredictedRT;
    }

    public List<Double> getLastRT() {
        return this.LastRT;
    }

    public void addLastestRT(Double RT) {
        LastRT.add(RT);
        if (LastRT.size() > 50) {
            LastRT.remove(0);
        }
    }

}
