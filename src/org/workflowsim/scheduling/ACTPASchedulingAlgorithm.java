/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.workflowsim.scheduling;

import csim01.Item;
import csim01.KnnClassifier;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;

import weka.classifiers.trees.RandomForest;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.avro.generic.GenericData;
import weka.classifiers.Classifier;
//import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

/*
 * @author HaoLee
 */
public class ACTPASchedulingAlgorithm extends BaseSchedulingAlgorithm {

    public ACTPASchedulingAlgorithm() {
        super();
    }
    /**
     * the check point list.
     */
    private final List<Boolean> hasChecked = new ArrayList<>();

    @Override
    public void run() {
        for (Iterator it = getCloudletList().iterator(); it.hasNext();) {
            Cloudlet cloudlet = (Cloudlet) it.next();            
                // Regression Predict
            double po = predictRequestPowerConsume(cloudlet);
            double ram = predictRequestRamUsage(cloudlet);
            double cpu = predictRequestCpuUsage(cloudlet);

            //1st KNN using integer
            //int label = knnClassifier1(po, cpu, ram);
            //CondorVM vm = (CondorVM) getFittingVm1(label);
            
            //2st KNN using integer
            double label = adaBoostClassifier(po, cpu, ram);
            CondorVM vm = (CondorVM) getFittingVm(label);

            vm.setState(WorkflowSimTags.VM_STATUS_BUSY);
            cloudlet.setVmId(vm.getId());
            getScheduledList().add(cloudlet); 
        }
    }
    
    public CondorVM getMostSuitableVM() {
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
            if ((vm.getState() == WorkflowSimTags.VM_STATUS_BUSY)
                    && vm.getCurrentRequestedTotalMips() > cVm.getCurrentRequestedTotalMips()) {
                cVm = vm;
            }
        }
        return cVm;       
    }
    
    public CondorVM getFittingVm(double label) {
        double cpu,ram,po = 0;
        int vmSize = getVmList().size();
        CondorVM cVm = (CondorVM)getVmList().get(0);
        for (int j = 0; j < vmSize; j++) {
            CondorVM vm = (CondorVM) getVmList().get(j);
            cpu = (vm.getCurrentRequestedTotalMips() * 100) / vm.getMips();
            ram = (vm.getCurrentRequestedRam()* 100) / vm.getRam();        
            po = vm.getPowerConsume();
            double predictLabel = adaBoostClassifier(po,cpu,ram);
            if(predictLabel>=label)
            {
                cVm = vm;
                break;
            }
        }
        return cVm;  
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
    
    public double getVMUsage(CondorVM vm)
    {
        double mips,ram,storage = 0;
        
        mips = (vm.getCurrentRequestedTotalMips() * 100) / vm.getMips();
        ram = (vm.getCurrentRequestedRam()* 100) / vm.getRam();        
        storage = (vm.getCurrentAllocatedSize()* 100) / vm.getSize();        
        return (mips*0.5 +ram*0.3+storage*0.2);
    }

    public double predictRequestUsage(Cloudlet req, int type) {
        double res = 0;
        String path ="";
        if(type==1) // RAM
            path="D:/csim01/test/usage-ram.arff";
        if(type==2) // cpu
            path="D:/csim01/test/usage-cpu.arff";
        if(type==3) // storage
            path="D:/csim01/test/usage-storage.arff";
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(path)));
            data.setClassIndex(data.numAttributes() - 1);
            //build model
            RandomForest model = new RandomForest();
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
    
    public double predictRequestPowerConsume(Cloudlet req)
    {
        double res = 0;
        String path ="D:/csim01/ACTPA/RegData_Po.arff";
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(path)));
            data.setClassIndex(data.numAttributes() - 1);
            //build model
            RandomForest model = new RandomForest();
            model.buildClassifier(data); //the last instance with missing not used

            Instance test = new Instance(6);
            test.setValue(0, req.getCloudletLength());
            test.setValue(1, req.getCloudletOutputSize());
            test.setValue(2, req.getCloudletTotalLength());
            test.setValue(3, req.getCloudletFileSize());            
            test.setMissing(4);
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
    
    public double predictRequestCpuUsage(Cloudlet req)
    {
        double res = 0;
        String path ="D:/csim01/ACTPA/RegData_CPU.arff";
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(path)));
            data.setClassIndex(data.numAttributes() - 1);
            //build model
            RandomForest model = new RandomForest();
            model.buildClassifier(data); //the last instance with missing not used

            Instance test = new Instance(6);
            test.setValue(0, req.getCloudletLength());
            test.setValue(1, req.getCloudletOutputSize());
            test.setValue(2, req.getCloudletTotalLength());
            test.setValue(3, req.getCloudletFileSize());            
            test.setMissing(4);
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
    
    public double predictRequestRamUsage(Cloudlet req)
    {
        double res = 0;
        String path ="D:/csim01/ACTPA/RegData_RAM.arff";
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(path)));
            data.setClassIndex(data.numAttributes() - 1);
            //build model
            RandomForest model = new RandomForest();
            model.buildClassifier(data); //the last instance with missing not used

            Instance test = new Instance(5);
            test.setValue(0, req.getCloudletLength());
            test.setValue(1, req.getCloudletOutputSize());
            test.setValue(2, req.getCloudletTotalLength());
            test.setValue(3, req.getCloudletFileSize());            
            test.setMissing(4);
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
    
    public double adaBoostClassifier(double po, double cpu, double ram)
    {
        double res = 0;
        String path ="D:/csim01/ACTPA/KNNTrainingData.arff";
        try {
            Instances data = new Instances(new BufferedReader(new FileReader(path)));
   
            if (data.classIndex() == -1) {
                data.setClassIndex(data.numAttributes() - 1);
            }
           
            Instance test = new Instance(4);
            test.setValue(0, cpu);
            test.setValue(1, ram);
            test.setValue(2, po);            
            test.setMissing(3);
           
            Classifier ibk = new AdaBoostM1();
            ibk.buildClassifier(data);

            res = ibk.classifyInstance(test);
        }catch (Exception e) {
            res = 0;
        }
        return res;       
    }
    
    
    
}
