/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.workflowsim.scheduling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.functions.LinearRegression;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

/*
 * @author HaoLee
 */
public class PDOASchedulingAlgorithm extends BaseSchedulingAlgorithm {

    public PDOASchedulingAlgorithm() {
        super();
    }
    /**
     * the check point list.
     */
    private final List<Boolean> hasChecked = new ArrayList<>();

    @Override
    public void run() {

        //Log.printLine("Schedulin Cycle");
        int size = getCloudletList().size();
        hasChecked.clear();
        for (int t = 0; t < size; t++) {
            hasChecked.add(false);
        }
        for (int i = 0; i < size; i++) {
            int maxIndex = 0;
            Cloudlet maxCloudlet = null;
            for (int j = 0; j < size; j++) {
                Cloudlet cloudlet = (Cloudlet) getCloudletList().get(j);
                if (!hasChecked.get(j)) {
                    maxCloudlet = cloudlet;
                    maxIndex = j;
                    break;
                }
            }
            if (maxCloudlet == null) {
                break;
            }

            for (int j = 0; j < size; j++) {
                Cloudlet cloudlet = (Cloudlet) getCloudletList().get(j);
                if (hasChecked.get(j)) {
                    continue;
                }
                long length = cloudlet.getCloudletLength();
                if (length > maxCloudlet.getCloudletLength()) {
                    maxCloudlet = cloudlet;
                    maxIndex = j;
                }
            }
            hasChecked.set(maxIndex, true);

            int vmSize = getVmList().size();
            CondorVM firstIdleVm = null;//(CondorVM)getVmList().get(0);
            for (int j = 0; j < vmSize; j++) {
                CondorVM vm = (CondorVM) getVmList().get(j);
                if (vm.getState() == WorkflowSimTags.VM_STATUS_IDLE) {
                    firstIdleVm = vm;
                    break;
                }
            }
            if (firstIdleVm == null) {
                break;
            }
            for (int j = 0; j < vmSize; j++) {
                CondorVM vm = (CondorVM) getVmList().get(j);
                if ((vm.getState() == WorkflowSimTags.VM_STATUS_IDLE)
                        && vm.getCurrentRequestedTotalMips() > firstIdleVm.getCurrentRequestedTotalMips()) {
                    firstIdleVm = vm;

                }
            }
            firstIdleVm.setState(WorkflowSimTags.VM_STATUS_BUSY);
            maxCloudlet.setVmId(firstIdleVm.getId());
            getScheduledList().add(maxCloudlet);
            Log.printLine("Schedules " + maxCloudlet.getCloudletId() + " with "
                    + maxCloudlet.getCloudletLength() + " to VM " + firstIdleVm.getId()
                    + " with " + firstIdleVm.getCurrentRequestedTotalMips());

        }
    }
    
    public CondorVM getMostSuitableVM(double usagePercentage) {
        int vmSize = getVmList().size();
        CondorVM cVm = null;//(CondorVM)getVmList().get(0);
        for (int j = 0; j < vmSize; j++) {
            CondorVM vm = (CondorVM) getVmList().get(j);
            if (vm.getState() == WorkflowSimTags.VM_STATUS_IDLE) {
                cVm = vm;
                break;
            }
        }
        for (int j = 0; j < vmSize; j++) {
            CondorVM vm = (CondorVM) getVmList().get(j);
            if ((vm.getState() == WorkflowSimTags.VM_STATUS_IDLE)
                    && vm.getCurrentRequestedTotalMips() > cVm.getCurrentRequestedTotalMips()) {
                cVm = vm;
            }
        }
        return cVm;
        //cVm.getCurrentRequestedTotalMips()
        //cVm.getCurrentAllocatedSize()
        //cVm.getCurrentAllocatedBw()
    }
    
    public double getVMUsage(CondorVM vm, int type)
    {
        double res = 0;
        if(type==2) // CPU
        {
            res = (vm.getCurrentRequestedTotalMips() * 100) / vm.getMips();
        }
        else if(type==1) // ram
        {
            res = (vm.getCurrentRequestedRam()* 100) / vm.getRam();
        }
        else if(type==3) // storage
        {
            res = (vm.getCurrentAllocatedSize()* 100) / vm.getSize();
        }
        return res;
    }

    public double predictRequestUsage(Cloudlet req, int type) {
        double res = 0;
        String path ="";
        if(type==1) // RAM
            path="G:/cloudsim/test/usage-ram.arff";
        if(type==2) // cpu
            path="G:/cloudsim/test/usage-cpu.arff";
        if(type==3) // storage
            path="G:/cloudsim/test/usage-storage.arff";
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(path)));
            data.setClassIndex(data.numAttributes() - 1);
            //build model
            LinearRegression model = new LinearRegression();
            model.buildClassifier(data); //the last instance with missing not used

            Instance test = new Instance(6);
            test.setValue(0, req.getAverageSize());
            test.setValue(1, req.getCloudletOutputSize());
            test.setValue(2, req.getCloudletFileSize());
            test.setValue(3, req.getCloudletFileSize());
            test.setValue(4, req.getProcessingCost());
            test.setMissing(5);
            data.add(test);
            //classify the last instance
            Instance myHouse = data.lastInstance();
            res = model.classifyInstance(myHouse);
            //System.out.println("My house (" + myHouse + "): " + price);
        } catch (Exception e) {
            res = 0;
        }
        return res;
    }
}
