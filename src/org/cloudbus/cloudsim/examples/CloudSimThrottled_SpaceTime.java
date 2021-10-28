/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.examples;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
//import org.cloudbus.cloudsim.examples.ResponseTimeDatacenterBroker;
import org.cloudbus.cloudsim.ex.DatacenterBrokerEX;
import org.cloudbus.cloudsim.CloudletSchedulerTimeShared;
import org.cloudbus.cloudsim.CloudletSchedulerSpaceShared;
import org.cloudbus.cloudsim.Datacenter;
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
import org.cloudbus.cloudsim.VmSchedulerTimeShared;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

/**
 *
 * @author Admin
 */
public class CloudSimThrottled_SpaceTime {

    /**
     * The cloudlet list.
     */
    private static List<Cloudlet> cloudletList;

    /**
     * The vmlist.
     */
    private static List<Vm> vmlist;

    /**
     * Creates main() to run this example
     */
    public static void main(String[] args) {

        Log.printLine("Bat dau chuong trinh mo phong thuat toan Throttled");

        try {
            // First step: Initialize the CloudSim package. It should be called
            // before creating any entities.
            int num_user = 1;   // number of cloud users
            Calendar calendar = Calendar.getInstance();
            boolean trace_flag = false;  // mean trace events

            // Initialize the CloudSim library
            CloudSim.init(num_user, calendar, trace_flag);

            // Second step: Create Datacenters
            // Datacenters are the resource providers in CloudSim. We need at list one of them to run a CloudSim simulation
            Datacenter datacenter0 = createDatacenter("Datacenter_0");
            //Datacenter datacenter1 = createDatacenter("Datacenter_1");

            //Third step: Create Broker
            //DatacenterBroker broker = createBroker();
            //ActiveVmMonitoringLoadBalancer broker = createBroker();   
            DatacenterBrokerEX broker = createBroker();
            int brokerId = broker.getId();

            //Fourth step: Create one virtual machine
            vmlist = new ArrayList<Vm>();

            //VM description
            int vmid = 0;
            int mips = 4000;
            long size = 10000; //image size (MB)
            int ram = 1024; //vm memory (MB)
            long bw = 1024;
            int pesNumber = 1; //number of cpus
            String vmm = "Xen"; //VMM name

            //create two VMs
            Vm vm1 = new Vm(vmid, brokerId, mips * 4, pesNumber * 4, ram * 4, bw, size, vmm, new CloudletSchedulerTimeShared());
            //broker.vmIndexTable.put(vmid, 0);

            //the second VM will have twice the priority of VM1 and so will receive twice CPU time
            vmid++;
            Vm vm2 = new Vm(vmid, brokerId, mips * 2, pesNumber * 2, ram * 2, bw, size, vmm, new CloudletSchedulerTimeShared());
            //broker.vmIndexTable.put(vmid, 0);

            vmid++;
            Vm vm3 = new Vm(vmid, brokerId, mips * 1, pesNumber * 2, ram * 1, bw, size, vmm, new CloudletSchedulerTimeShared());
            //broker.vmIndexTable.put(vmid, 0);

            //vmid++;
            //Vm vm4 = new Vm(vmid, brokerId, mips*2, pesNumber*2, ram*2, bw, size, vmm, new CloudletSchedulerTimeShared());
            //broker.vmIndexTable.put(vmid, 0);
            //vmid++;
            //Vm vm5 = new Vm(vmid, brokerId, mips*1, pesNumber*1, ram*1, bw, size, vmm, new CloudletSchedulerTimeShared());
            //broker.vmIndexTable.put(vmid, 0);
            //vmid++;
            //Vm vm6 = new Vm(vmid, brokerId, mips*1, pesNumber*1, ram*1, bw, size, vmm, new CloudletSchedulerTimeShared());
            //broker.vmIndexTable.put(vmid, 0);
            //add the VMs to the vmList
            vmlist.add(vm1);
            vmlist.add(vm2);
            vmlist.add(vm3);
            //vmlist.add(vm4);
            //vmlist.add(vm5);
            //vmlist.add(vm6);

            //submit vm list to the broker
            broker.submitVmList(vmlist);

            //Fifth step: Create ten Cloudlets
            cloudletList = new ArrayList<Cloudlet>();

            //Cloudlet properties
            int id = 0;
            long length = 1000;
            long fileSize = 300;
            long outputSize = 300;
            UtilizationModel utilizationModel = new UtilizationModelFull();

            Cloudlet cloudlet1 = new Cloudlet(id, length * 2, pesNumber * 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet1.setUserId(brokerId);

            id++;
            Cloudlet cloudlet2 = new Cloudlet(id, length * 3, pesNumber * 2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet2.setUserId(brokerId);

            id++;
            Cloudlet cloudlet3 = new Cloudlet(id, length * 4, pesNumber * 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet3.setUserId(brokerId);

            id++;
            Cloudlet cloudlet4 = new Cloudlet(id, length * 3, pesNumber * 2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet4.setUserId(brokerId);

            id++;
            Cloudlet cloudlet5 = new Cloudlet(id, length * 2, pesNumber * 2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet5.setUserId(brokerId);

            id++;
            Cloudlet cloudlet6 = new Cloudlet(id, length * 2, pesNumber * 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet6.setUserId(brokerId);

            id++;
            Cloudlet cloudlet7 = new Cloudlet(id, length * 1, pesNumber * 2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet7.setUserId(brokerId);

            id++;
            Cloudlet cloudlet8 = new Cloudlet(id, length * 3, pesNumber * 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet8.setUserId(brokerId);

            id++;
            Cloudlet cloudlet9 = new Cloudlet(id, length * 5, pesNumber * 2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet9.setUserId(brokerId);

            id++;
            Cloudlet cloudlet10 = new Cloudlet(id, length * 2, pesNumber * 1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            cloudlet10.setUserId(brokerId);
            id++;
            /* Cloudlet cloudlet11 = new Cloudlet(id, length *2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            			cloudlet11.setUserId(brokerId);
                                   
            						id++;
            Cloudlet cloudlet12 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            			cloudlet12.setUserId(brokerId);
                                    
                                    id++;
            Cloudlet cloudlet13 = new Cloudlet(id, length*4, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet13.setUserId(brokerId);
                                    
                                    id++;
           Cloudlet cloudlet14 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet14.setUserId(brokerId);
                                    
                                    id++;
           Cloudlet cloudlet15 = new Cloudlet(id, length*2, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet15.setUserId(brokerId);
                      
                                    id++;
           Cloudlet cloudlet16 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet16.setUserId(brokerId);
                                    
                                    id++;
            Cloudlet cloudlet17 = new Cloudlet(id, length*1, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet17.setUserId(brokerId);
                                    
                                    id++;
            Cloudlet cloudlet18 = new Cloudlet(id, length*3, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet18.setUserId(brokerId);
                                    
                                    id++;
             Cloudlet cloudlet19 = new Cloudlet(id, length*5, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet19.setUserId(brokerId);
                                    
                                    id++;
              Cloudlet cloudlet20 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    cloudlet20.setUserId(brokerId);
                                    id++;
             Cloudlet cloudlet21 = new Cloudlet(id, length *2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                        			cloudlet21.setUserId(brokerId);
                                               
                        						id++;
             Cloudlet cloudlet22 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                        			cloudlet22.setUserId(brokerId);
                                                
                                                id++;
             Cloudlet cloudlet23 = new Cloudlet(id, length*4, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet23.setUserId(brokerId);
                                                
                                                id++;
             Cloudlet cloudlet24 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet24.setUserId(brokerId);
                                                
                                                id++;
             Cloudlet cloudlet25 = new Cloudlet(id, length*2, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet25.setUserId(brokerId);
                                                
                                                id++;
              Cloudlet cloudlet26 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet26.setUserId(brokerId);
                                                
                                                id++;
             Cloudlet cloudlet27 = new Cloudlet(id, length*1, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet27.setUserId(brokerId);
                                                
                                                id++;
             Cloudlet cloudlet28 = new Cloudlet(id, length*3, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet28.setUserId(brokerId);
                                                
                                                id++;
             Cloudlet cloudlet29 = new Cloudlet(id, length*5, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet29.setUserId(brokerId);
                                                
                                                id++;
            Cloudlet cloudlet30 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                cloudlet30.setUserId(brokerId);
                                                id++;
           Cloudlet cloudlet31 = new Cloudlet(id, length *2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    			cloudlet31.setUserId(brokerId);
                                                           
                                    						id++;
          Cloudlet cloudlet32 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                    			cloudlet32.setUserId(brokerId);
                                                            
                                                            id++;
          Cloudlet cloudlet33 = new Cloudlet(id, length*4, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet33.setUserId(brokerId);
                                                            
                                                            id++;
           Cloudlet cloudlet34 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet34.setUserId(brokerId);
                                                            
                                                            id++;
            Cloudlet cloudlet35 = new Cloudlet(id, length*2, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet35.setUserId(brokerId);
                                                            
                                                            id++;
           Cloudlet cloudlet36 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet36.setUserId(brokerId);
                                                            
                                                            id++;
          Cloudlet cloudlet37 = new Cloudlet(id, length*1, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet37.setUserId(brokerId);
                                                            
                                                            id++;
          Cloudlet cloudlet38 = new Cloudlet(id, length*3, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet38.setUserId(brokerId);
                                                            
                                                            id++;
           Cloudlet cloudlet39 = new Cloudlet(id, length*5, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet39.setUserId(brokerId);
                                                            
                                                            id++;
           Cloudlet cloudlet40 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            cloudlet40.setUserId(brokerId);
                                                            id++;
      /*  Cloudlet cloudlet41 = new Cloudlet(id, length *2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                			cloudlet41.setUserId(brokerId);
                                                                       
                                                						id++;
            Cloudlet cloudlet42 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                			cloudlet42.setUserId(brokerId);
                                                                        
                                                                        id++;
            Cloudlet cloudlet43 = new Cloudlet(id, length*4, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet43.setUserId(brokerId);
                                                                        
                                                                        id++;
           Cloudlet cloudlet44 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet44.setUserId(brokerId);
                                                                        
                                                                        id++;
            Cloudlet cloudlet45 = new Cloudlet(id, length*2, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet45.setUserId(brokerId);
                                                                        
                                                                        id++;
            Cloudlet cloudlet46 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet46.setUserId(brokerId);
                                                                        
                                                                        id++;
             Cloudlet cloudlet47 = new Cloudlet(id, length*1, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet47.setUserId(brokerId);
                                                                        
                                                                        id++;
           Cloudlet cloudlet48 = new Cloudlet(id, length*3, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet48.setUserId(brokerId);
                                                                        
                                                                        id++;
            Cloudlet cloudlet49 = new Cloudlet(id, length*5, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet49.setUserId(brokerId);
                                                                        id++;
           Cloudlet cloudlet50 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                        cloudlet50.setUserId(brokerId);
                                                                        id++;
 	Cloudlet cloudlet51 = new Cloudlet(id, length *2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            			cloudlet51.setUserId(brokerId);
                                                                                   
                                                            						id++;
             Cloudlet cloudlet52 = new Cloudlet(id, length*3, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                            			cloudlet52.setUserId(brokerId);
                                                                                    
                                                                                    id++;
            Cloudlet cloudlet53 = new Cloudlet(id, length*4, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                                    cloudlet53.setUserId(brokerId);
                                                                                    
                                                                                    id++;
             Cloudlet cloudlet54 = new Cloudlet(id, length*3, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                                                                                    cloudlet54.setUserId(brokerId);
                                                                                    
                                                                                    id++;
            Cloudlet cloudlet55 = new Cloudlet(id, length*2, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                       cloudlet55.setUserId(brokerId);
                                                                                    
                                  id++;
           Cloudlet cloudlet56 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                         cloudlet56.setUserId(brokerId);
                                  id++;
           Cloudlet cloudlet57 = new Cloudlet(id, length*1, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                     cloudlet57.setUserId(brokerId);
                                                                                    
                            id++;
           Cloudlet cloudlet58 = new Cloudlet(id, length*3, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                         cloudlet58.setUserId(brokerId);
                    
                           id++;
            Cloudlet cloudlet59 = new Cloudlet(id, length*5, pesNumber*2, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                         cloudlet59.setUserId(brokerId);
                                                                                    
                           id++;
            Cloudlet cloudlet60 = new Cloudlet(id, length*2, pesNumber*1, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
                           cloudlet60.setUserId(brokerId);
                                                                                    id++;
             */
            //add the cloudlets to the list
            cloudletList.add(cloudlet1);
            cloudletList.add(cloudlet2);
            cloudletList.add(cloudlet3);
            cloudletList.add(cloudlet4);
            cloudletList.add(cloudlet5);
            cloudletList.add(cloudlet6);
            cloudletList.add(cloudlet7);
            cloudletList.add(cloudlet8);
            cloudletList.add(cloudlet9);
            cloudletList.add(cloudlet10);
            /*  cloudletList.add(cloudlet11);
			cloudletList.add(cloudlet12);
            cloudletList.add(cloudlet13);
            cloudletList.add(cloudlet14);
            cloudletList.add(cloudlet15);
            cloudletList.add(cloudlet16);
            cloudletList.add(cloudlet17);
            cloudletList.add(cloudlet18);
            cloudletList.add(cloudlet19);
           cloudletList.add(cloudlet20);
         cloudletList.add(cloudlet21);
			cloudletList.add(cloudlet22);
            cloudletList.add(cloudlet23);
            cloudletList.add(cloudlet24);
            cloudletList.add(cloudlet25);
            cloudletList.add(cloudlet26);
            cloudletList.add(cloudlet27);
            cloudletList.add(cloudlet28);
            cloudletList.add(cloudlet29);
            cloudletList.add(cloudlet30);
           cloudletList.add(cloudlet31);
			cloudletList.add(cloudlet32);
            cloudletList.add(cloudlet33);
            cloudletList.add(cloudlet34);
            cloudletList.add(cloudlet35);
            cloudletList.add(cloudlet36);
            cloudletList.add(cloudlet37);
            cloudletList.add(cloudlet38);
            cloudletList.add(cloudlet39);
            cloudletList.add(cloudlet40);
           /*cloudletList.add(cloudlet41);
			cloudletList.add(cloudlet42);
            cloudletList.add(cloudlet43);
            cloudletList.add(cloudlet44);
            cloudletList.add(cloudlet45);
            cloudletList.add(cloudlet46);
            cloudletList.add(cloudlet47);
            cloudletList.add(cloudlet48);
            cloudletList.add(cloudlet49);
            cloudletList.add(cloudlet50);
           cloudletList.add(cloudlet51);
			cloudletList.add(cloudlet52);
            cloudletList.add(cloudlet53);
            cloudletList.add(cloudlet54);
            cloudletList.add(cloudlet55);
            cloudletList.add(cloudlet56);
            cloudletList.add(cloudlet57);
            cloudletList.add(cloudlet58);
            cloudletList.add(cloudlet59);
            cloudletList.add(cloudlet60);*/
            //submit cloudlet list to the broker
            broker.submitCloudletList(cloudletList);

            //bind the cloudlets to the vms. This way, the broker
            //will submit the bound cloudlets only to the specific VM
            //broker.bindCloudletToVm(cloudlet1.getCloudletId(),vm1.getId());
            //broker.bindCloudletToVm(cloudlet2.getCloudletId(),vm2.getId());
            // Sixth step: Starts the simulation
            CloudSim.startSimulation();

            // Final step: Print results when simulation is over
            List<Cloudlet> newList = broker.getCloudletReceivedList();

            CloudSim.stopSimulation();

            printCloudletList(newList);

            //Print the debt of each user to each datacenter
//datacenter0.printDebts();
            //datacenter1.printDebts();
            Log.printLine("Mo phong thuat toan Throttled da hoan thanh!");
        } catch (Exception e) {
            e.printStackTrace();
            Log.printLine("The simulation has been terminated due to an unexpected error");
        }
    }

    private static Datacenter createDatacenter(String name) {

        // Here are the steps needed to create a PowerDatacenter:
        // 1. We need to create a list to store
        //    our machine
        List<Host> hostList = new ArrayList<Host>();

        // 2. A Machine contains one or more PEs or CPUs/Cores.
        // In this example, it will have only one core.
        List<Pe> peList1 = new ArrayList<Pe>();

        int mips = 10000;

        // 3. Create PEs and add these into a list.
        peList1.add(new Pe(0, new PeProvisionerSimple(mips * 4))); // need to store Pe id and MIPS Rating
        peList1.add(new Pe(1, new PeProvisionerSimple(mips * 4)));
        peList1.add(new Pe(2, new PeProvisionerSimple(mips * 4)));
        peList1.add(new Pe(3, new PeProvisionerSimple(mips * 4)));
        peList1.add(new Pe(4, new PeProvisionerSimple(mips * 4)));

        // 4. Create Hosts with its id and list of PEs and add them to the list of machines
        // This is our first machine
        int hostId = 0;
        int ram1 = 10240; //host memory (MB)
        long storage1 = 1024000; //host storage
        int bw1 = 10000;

        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram1),
                        new BwProvisionerSimple(bw1),
                        storage1,
                        peList1,
                        new VmSchedulerSpaceShared(peList1)
                )
        );

        // This is our second machine
        List<Pe> peList2 = new ArrayList<Pe>();

        peList2.add(
                new Pe(0, new PeProvisionerSimple(mips * 2)));
        peList2.add(new Pe(1, new PeProvisionerSimple(mips * 2)));

        hostId++;
        int ram2 = 5120; //host memory (MB)
        long storage2 = 1044480; //host storage
        int bw2 = 10000;
        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram2),
                        new BwProvisionerSimple(bw2),
                        storage2,
                        peList2,
                        new VmSchedulerSpaceShared(peList2)
                )
        );
        // This is our third machine
        List<Pe> peList3 = new ArrayList<Pe>();

        peList3.add(new Pe(0, new PeProvisionerSimple(mips * 2)));
        peList3.add(new Pe(1, new PeProvisionerSimple(mips * 2)));
        hostId++;
        int ram3 = 12228;            // host memory (MB)
        long storage3 = 1044480;    // host storage
        int bw3 = 10000;
        hostList.add(
                new Host(
                        hostId,
                        new RamProvisionerSimple(ram3),
                        new BwProvisionerSimple(bw3),
                        storage3,
                        peList3,
                        new VmSchedulerSpaceShared(peList3)
                )
        );

        /*                List<Pe> peList4 = new ArrayList<Pe>();

		peList4.add(new Pe(0, new PeProvisionerSimple(mips*4)));
                peList4.add(new Pe(1, new PeProvisionerSimple(mips*4)));
                peList4.add(new Pe(2, new PeProvisionerSimple(mips*4)));
                peList4.add(new Pe(3, new PeProvisionerSimple(mips*4)));
                
                hostId++;
                int ram4 = 4096; //host memory (MB)
		long storage4 = 4194304; //host storage
		int bw4 = 1000000;
		hostList.add(
    			new Host(
    				hostId,
    				new RamProvisionerSimple(ram4),
    				new BwProvisionerSimple(bw4),
    				storage4,
    				peList4,
    				new VmSchedulerTimeShared(peList4)
    			)
    		); 
                
                //List<Pe> peList5 = new ArrayList<Pe>();
         */
        //peList5.add(new Pe(0, new PeProvisionerSimple(mips*2)));
        //peList5.add(new Pe(1, new PeProvisionerSimple(mips*2)));
        //hostId++;
        //int ram5 = 4096; //host memory (MB)
        //long storage5 = 4194304; //host storage
        //int bw5 = 1000000;
        //hostList.add(
        //	new Host(
        //		hostId,
        //		new RamProvisionerSimple(ram5),
        //		new BwProvisionerSimple(bw5),
        //		storage5,
        //		peList5,
        //		new VmSchedulerTimeShared(peList5)
        //	)
        //); 
        //List<Pe> peList6 = new ArrayList<Pe>();
        //peList6.add(new Pe(0, new PeProvisionerSimple(mips*1)));
        //hostId++;
        //int ram6 = 4096; //host memory (MB)
        //long storage6 = 4194304; //host storage
        //int bw6 = 1000000;
        //hostList.add(
        //	new Host(
        //		hostId,
        //		new RamProvisionerSimple(ram6),
        //		new BwProvisionerSimple(bw6),
        //		storage6,
        //		peList6,
        //		new VmSchedulerTimeShared(peList6)
        //	)
        //); 
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
        double costPerStorage = 0.001;	// the cost of using storage in this resource
        double costPerBw = 0.0;			// the cost of using bw in this resource
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

    //We strongly encourage users to develop their own broker policies, to submit vms and cloudlets according
    //to the specific rules of the simulated scenario
    /*private static ActiveVmMonitoringLoadBalancer createBroker(){

		ActiveVmMonitoringLoadBalancer broker = null;
		try {
			broker = new ActiveVmMonitoringLoadBalancer("BrokerActiveVm");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}
     */
    private static DatacenterBrokerEX createBroker() {

        DatacenterBrokerEX broker = null;
        try {
            broker = new DatacenterBrokerEX("BrokerActiveVm", 1000000);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }

    /**
     * Prints the Cloudlet objects
     *
     * @param list list of Cloudlets
     */
    private static void printCloudletList(List<Cloudlet> list) {
        int size = list.size();
        double kqxltb = 0;
        double kqdutb = 0;
        Cloudlet cloudlet;

        String indent = "    ";
        Log.printLine();
        Log.printLine("========== OUTPUT  ==========");
        Log.printLine("Cloudlet ID" + indent + indent + "STATUS" + indent + indent
                + "Data center ID" + indent + "VM ID" + indent + "Time" + indent + "Start Time" + indent + "Finish Time");

        DecimalFormat dft = new DecimalFormat("###.###");

        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            Log.print(indent + cloudlet.getCloudletId() + indent + indent);

            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                Log.print("SUCCESS");

                Log.printLine(indent + indent + cloudlet.getResourceId() 
                        + indent + indent + indent + cloudlet.getVmId()
                        + indent + indent + dft.format(cloudlet.getActualCPUTime() * 1000) 
                        + indent + indent + dft.format(cloudlet.getExecStartTime() * 1000)
                        + indent + indent + dft.format(cloudlet.getFinishTime() * 1000));
                kqxltb = kqxltb + cloudlet.getActualCPUTime() * 1000;
                kqdutb = kqdutb + cloudlet.getFinishTime() * 1000;
            } else {
                Log.printLine("Cloudlet chưa được thực thi !");
            }
        }
        Log.printLine("Thoi gian xu ly du lieu trung binh la: " + dft.format(kqxltb / size));
        Log.printLine("Thoi gian dap ung du lieu trung binh la: " + dft.format(kqdutb / size));
    }
}
