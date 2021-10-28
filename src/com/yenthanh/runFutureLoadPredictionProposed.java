/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.yenthanh;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
import org.cloudbus.cloudsim.futureLoadPredictionProposed;
import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Host;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Pe;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.VmAllocationPolicySimple;
import org.cloudbus.cloudsim.VmSchedulerSpaceShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import java.sql.SQLException;
import java.sql.ResultSet;

/**
 *
 * @author Admin
 */
public class runFutureLoadPredictionProposed {

    private static int allocationType;
    private static String AlgoName;
    /**
     * The cloudlet list.
     */
    private static List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<Vm> vmlist;

    private static List<Vm> createVM(int userId, int vms, int idShift) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<Vm> list = new LinkedList<Vm>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 4096; //vm memory (MB)
        int mips = 3200;
        long bw = 1000;
        int pesNumber = 4; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        Vm[] vm = new Vm[vms];
        //VM1
        int i = 0;
        vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
        list.add(vm[i]);
        //VM2
        ram = 2048;
        pesNumber = 2;
        mips = 16000;
        i++;
        vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
        list.add(vm[i]);
        //VM3
        ram = 1024;
        pesNumber = 2;
        mips = 8100;
        i++;
        vm[i] = new Vm(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerSpaceShared());
        list.add(vm[i]);
        return list;
    }

    private static List<Cloudlet> createCloudlet(int userId, int cloudlets, int idShift) {
        // Creates a container to store Cloudlets
        LinkedList<Cloudlet> list = new LinkedList<Cloudlet>();

        //cloudlet parameters
        long length = 95000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 2;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        Cloudlet[] cloudlet = new Cloudlet[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            if (i < 100) {
                length = length + 5000;
            } else {
                length = length - 5000;
            }
            cloudlet[i] = new Cloudlet(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }

    ////////////////////////// STATIC METHODS ///////////////////////
    /**
     * Creates main() to run this example
     */
    public static void main(String[] args) {
        int nCloudlets = 700;
        allocationType = 2;
        String algoType = algoType();
        AlgoName = "Future Load Prediction Proposed";
        Log.printLine("Starting Demo... [" + AlgoName + "] " + algoType + "  ");
        Log.printLine("Number of Cloudlets (Requests) is " + nCloudlets);

        try {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 1;   // number of grid users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            //Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
            @SuppressWarnings("unused")
            Datacenter datacenter0 = createDatacenter("Datacenter_0");

            //Third step: Create Broker
            futureLoadPredictionProposed broker = createBroker("Broker_0");
            int brokerId = broker.getId();

            //Fourth step: Create VMs and Cloudlets and send them to broker
            vmlist = createVM(brokerId, 3, 0); //gọi phương thức Tạo máy ảo
            //userid lấy luôn là brokerid
            cloudletList = createCloudlet(brokerId, nCloudlets, 0); //gọi phương thức Tạo 10 cloudlets

            broker.submitVmList(vmlist);

            broker.submitCloudletList(cloudletList);
            //broker.submitCloudlets();

            // Fifth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();
            CloudSim.stopSimulation();

            printCloudletList(newList);

            Log.printLine("\nDemo finished! Với số lượng cloudlet là " + nCloudlets + "    !! ");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static Datacenter createDatacenter(String name) {
        //Tạo 3 host

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store one or more
        //    Machines
        List<Host> hostList = new ArrayList<Host>(); //Lưu danh sách host

        // 2. A Machine contains one or more PEs or CPUs/Cores. Therefore, should
        //    create a list to store these PEs before creating
        //    a Machine.
        List<Pe> peList1 = new ArrayList<Pe>(); //Lưu danh sách core (PE)
        int mips = 40000; //Tốc độ của core

        // 3. Create PEs and add these into the list. Máy 1 chứa 4 core
        //for a quad-core machine, a list of 4 PEs is required:
        peList1.add(new Pe(0, new PeProvisionerSimple(mips))); // need to store Pe id and MIPS Rating
        peList1.add(new Pe(1, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(2, new PeProvisionerSimple(mips)));
        peList1.add(new Pe(3, new PeProvisionerSimple(mips)));

        //Another list, for a dual-core machine. Máy 2 chứa 2 core
        List<Pe> peList2 = new ArrayList<Pe>();
        mips = 20000; //Tốc độ của core
        peList2.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList2.add(new Pe(1, new PeProvisionerSimple(mips)));

        //Another list, for a dual-core machine. Máy 3 chứa 1 core
        List<Pe> peList3 = new ArrayList<Pe>();
        mips = 10000; //Tốc độ của core
        peList3.add(new Pe(0, new PeProvisionerSimple(mips)));
        peList3.add(new Pe(1, new PeProvisionerSimple(mips)));

        //4. Create Hosts with its id and list of PEs and add them to the list of machines
        int hostId = 0;
        int ram = 4096; //host memory (MB)
        long storage = 1000000; //host storage
        int bw = 10000;

        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList1,
                        new VmSchedulerSpaceShared(peList1)
                )
        ); // This is our first machine

        hostId++;
        ram = 12288; //host memory (MB)
        storage = 4000000; //host storage

        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList2,
                        new VmSchedulerSpaceShared(peList2)
                )
        ); // Second machine

        hostId++;
        ram = 8192; //host memory (MB)
        storage = 2000000; //host storage
        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram),
                        new BwProvisionerSimple(bw),
                        storage,
                        peList2,
                        new VmSchedulerSpaceShared(peList3)
                )
        ); // Third machine
        // 5. Create a DatacenterCharacteristics object that stores the
        //    properties of a data center: architecture, OS, list of
        //    Machines, allocation policy: time- or space-shared, time zone
        //    and its price (G$/Pe time unit).
        String arch = "x86";      // system architecture
        String os = "Linux";          // operating system
        String vmm = "Xen";
        double time_zone = 10.0;         // time zone this resource located
        double cost = 3.0;              // the cost of using processing in this resource
        double costPerMem = 0.05;		// the cost of using memory in this resource
        double costPerStorage = 0.1;	// the cost of using storage in this resource
        double costPerBw = 0.1;			// the cost of using bw in this resource
        LinkedList<Storage> storageList = new LinkedList<Storage>();	//we are not adding SAN devices by now

        DatacenterCharacteristics characteristics = new DatacenterCharacteristics(
                arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

        // 6. Finally, we need to create a PowerDatacenter object.
        Datacenter datacenter = null;
        try {
            datacenter = new Datacenter(name, characteristics, new VmAllocationPolicySimple(hostList), storageList, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datacenter;
    }

    public static futureLoadPredictionProposed createBroker(String name) {

        futureLoadPredictionProposed broker = null;
        try {
            broker = new futureLoadPredictionProposed(name, allocationType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }
    //Random number 

    public static int showRandomInteger(int aStart, int aEnd, Random aRandom) {
        if (aStart > aEnd) {
            throw new IllegalArgumentException("Start cannot exceed End.");
        }
        //get the range, casting to long to avoid overflow problems
        long range = (long) aEnd - (long) aStart + 1;
        // compute a fraction of the range, 0 <= frac < range
        long fraction = (long) (range * aRandom.nextDouble());
        int randomNumber = (int) (fraction + aStart);
        return randomNumber;
    }

    /**
     * Prints the Cloudlet objects
     *
     * @param list list of Cloudlets
     */
    public static void printCloudletList(List<Cloudlet> list) {
        double time_actual = 0;
        double time_start = 0;
        double time_finish = 0;
        int size = list.size();
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT ==========");
        Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
                + "Data center ID" + indent + "VM ID" + indent + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.##");
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + indent + dft.format(cloudlet.getActualCPUTime())
                        + indent + indent + dft.format(cloudlet.getExecStartTime()) + indent + indent + indent + dft.format(cloudlet.getFinishTime()));
                time_actual = time_actual + cloudlet.getActualCPUTime();
                time_start = time_start + cloudlet.getExecStartTime();
                time_finish = time_finish + cloudlet.getFinishTime();
            }
        }
        //Log.printLine("Thoi gian xu ly du lieu trung binh la: "+ dft.format(time_actual/size));
        //Log.printLine("Thoi gian dap ung du lieu trung binh la: "+ dft.format(time_finish/size));
        Log.printLine(" Thời gian xử lý trung bình của " + size + " cloudlet = " + time_actual / size);
        Log.printLine(" Thời gian đáp ứng trung bình của " + size + " cloudlet = " + time_finish / size);
        Log.printLine(" Algo Typ:  " + algoType());
        Thanhresults info = new Thanhresults();
        info.setAlgoName(AlgoName);
        info.setAllocationType(algoType());
        info.setAvgExecTime(BigDecimal.valueOf(time_actual / size));
        info.setAvgRespTime(BigDecimal.valueOf(time_finish / size));
        info.setNoCloudlets(BigInteger.valueOf(size));
        boolean test = SaveInfo(info);
    }

    public static String algoType() {
        String res = "";
        if (allocationType == 1) {
            res = "Space-Space";
        } else if (allocationType == 2) {
            res = "Space-Time";
        } else if (allocationType == 3) {
            res = "Time-Space";
        } else if (allocationType == 4) {
            res = "Time-Time";
        }
        return res;
    }

    public static boolean SaveInfo(Thanhresults result) {
        boolean res = false;
        try {
            dbUtil db = new dbUtil();
            String sql = "SELECT `id` FROM `thanhresults` WHERE `AlgoName`='" + result.getAlgoName() + "' AND `AllocationType`='" + result.getAllocationType() + "' AND `noCloudlets`=" + result.getNoCloudlets().toString();
            ResultSet rs = db.query(sql);
            rs.next();
            int id = 0;
            if (rs.first()) {
                id = rs.getInt("id");
            } else {
                id = 0;
            }
            rs.close();
            if (id > 0) {
                sql = "UPDATE `thanhresults` SET `AlgoName`='" + result.getAlgoName() + "',`noCloudlets`=" + result.getNoCloudlets() + ",`AllocationType`='" + result.getAllocationType() + "',`AvgExecTime`=" + result.getAvgExecTime() + ",`AvgRespTime`=" + result.getAvgRespTime() + ", `RunTime`=NOW() WHERE `id`=" + id + "";
                db.updateQuery(sql);
            } else {
                sql = "INSERT INTO `thanhresults`(`AlgoName`,`noCloudlets`,`AllocationType`,`AvgExecTime`,`AvgRespTime`,`RunTime`)";
                sql = sql + "VALUE('" + result.getAlgoName() + "', " + result.getNoCloudlets() + ",'" + result.getAllocationType() + "'," + result.getAvgExecTime() + "," + result.getAvgRespTime() + ",NOW())";
                db.updateQuery(sql);
            }
            db.disconnect();
            res = true;
        } catch (SQLException ex) {
            // handle any errors
            System.out.println("SQLException: " + ex.getMessage());
            System.out.println("SQLState: " + ex.getSQLState());
            System.out.println("VendorError: " + ex.getErrorCode());
        }
        return res;
    }
}
