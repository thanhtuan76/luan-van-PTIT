/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.workflowsim.scheduling;

import java.util.ArrayList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.Log;
import org.workflowsim.CondorVM;
import org.workflowsim.WorkflowSimTags;
import weka.associations.tertius.IndividualInstance;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.supervised.attribute.AddClassification;
import weka.clusterers.SimpleKMeans;

/**
 *
 * @author HaoLee
 */
public class RCBASchedulingAlgorithm extends BaseSchedulingAlgorithm {

    public RCBASchedulingAlgorithm() {
        super();
    }

    @Override
    public void run() {
        // Module 3;
        int size = getCloudletList().size();

        for (int i = 0; i < size; i++) {
            Cloudlet cloudlet = (Cloudlet) getCloudletList().get(i);
            if (cloudlet == null) {
                break;
            }
            String vm_class = predictRequestNB(cloudlet);
            CondorVM firstIdleVm = getMostFreeVM(vm_class);
            if (firstIdleVm == null) {
                break;
            }
            firstIdleVm.setState(WorkflowSimTags.VM_STATUS_BUSY);
            cloudlet.setVmId(firstIdleVm.getId());
            getScheduledList().add(cloudlet);
            Log.printLine("Schedules " + cloudlet.getCloudletId() + " with "
                    + cloudlet.getCloudletLength() + " to VM " + firstIdleVm.getId()
                    + " with " + firstIdleVm.getCurrentRequestedTotalMips());

        }
    }

    public CondorVM getMostFreeVM(String vmClass) {
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

    public int getMostFreeVM1(String vmClass) {
       int index =0;
        try {
            SimpleKMeans kmeans = new SimpleKMeans();
            kmeans.setSeed(10);

            //important parameter to set: preserver order, number of cluster.
            kmeans.setPreserveInstancesOrder(true);
            kmeans.setNumClusters(5);

            Instances data = getVmData();

            kmeans.buildClusterer(data);

            // This array returns the cluster number (starting with 0) for each instance
            // The array has as many elements as the number of instances
            int[] assignments = kmeans.getAssignments();

            int i = 0;
            int k =0;
            switch(vmClass)
            {
                case "1":
                    k=1;
                    break;
                case "2":
                    k=1;
                    break;
                case "3":
                    k=2;
                    break;
                case "4":
                    k=2;
                    break;
                case "5":
                    k=3;
                    break;
                case "6":
                    k=3;
                    break;
                case "7":
                    k=4;
                    break;
                case "8":
                    k=4;
                    break;
                case "9":
                    k=5;
                    break;
            }
            for (int clusterNum : assignments) {
                //System.out.printf("Instance %d -> Cluster %d \n", i, clusterNum);
                if(k==clusterNum)
                {
                    index = i;
                    break;
                }
                i++;
            }            
        } catch (Exception e) {
            index=0;
        }
        return index;
    }

    public Instances getVmData() {
        try {
            DataSource source = new DataSource("D:/csim01/test/vm.arff");
            Instances data = source.getDataSet();
            data.delete(0);
            int vmSize = getVmList().size();
            for (int i = 0; i < vmSize; i++) {
                CondorVM vm = (CondorVM) getVmList().get(i);
                Instance insta = new Instance(3);
                insta.setValue(0, vm.getCurrentRequestedTotalMips());
                insta.setValue(1, vm.getCurrentAllocatedSize());
                insta.setValue(2, vm.getCurrentAllocatedBw());
                data.add(insta);
            }
            return data;
        } catch (Exception e) {
            return null;
        }
    }

    public String predictRequestNB(Cloudlet req) {
        String res = "0";
        try {
            DataSource source = new DataSource("D:/csim01/test/cloudlet.arff");
            Instances traindata = source.getDataSet();
            traindata.setClassIndex(traindata.numAttributes() - 1);

            /**
             * naive bayes classifier
             */
//            NaiveBayes nb = new NaiveBayes();
            NaiveBayes nb = new NaiveBayes();
            nb.buildClassifier(traindata);
            /**
             * load test data
             */
            DataSource source2 = new DataSource("D:/csim01/test/cloudlet-unknown.arff");
            Instances testdata = source2.getDataSet();
            Instance test = new Instance(6);
            test.setValue(0, req.getAverageSize());
            test.setValue(1, req.getCloudletOutputSize());
            test.setValue(2, req.getCloudletFileSize());
            test.setValue(3, req.getCloudletFileSize());
            test.setValue(4, req.getProcessingCost());
            test.setMissing(5);
            testdata.add(test);
            testdata.setClassIndex(testdata.numAttributes() - 1);

            /**
             * make prediction by naive bayes classifier
             */
            int j = testdata.numInstances() - 1;
            double actualClass = testdata.instance(j).classValue();
            //System.out.println(actualClass);
            String actual = testdata.classAttribute().value((int) actualClass);
            //System.out.println(actual);
            Instance newInst = testdata.instance(j);
            //System.out.println("actual class:" + newInst.stringValue(newInst.numAttributes() - 1));
            double preNB = nb.classifyInstance(newInst);
            res = testdata.classAttribute().value((int) preNB);
            //System.out.println(actual + "," + predString);

        } catch (Exception e) {
            res = "0";
        }
        return res;
    }
}
