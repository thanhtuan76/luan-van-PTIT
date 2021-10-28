/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim;

import java.util.LinkedList;
import java.util.List;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.SimEntity;
import org.cloudbus.cloudsim.core.SimEvent;

/**
 *
 * @author Admin
 */
public class GlobalBrokerEx extends SimEntity {

    private static final int CREATE_BROKER = 0;
    private List<VmExt> vmList;
    private List<CloudletExt> cloudletList;
    private ArimaDatacenterBroker broker;

    public GlobalBrokerEx(String name) {
        super(name);
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CREATE_BROKER:
                setBroker(createBroker(super.getName() + "_"));

                //Create VMs and Cloudlets and send them to broker
                setVmList(createVM(getBroker().getId(), 5, 100)); //creating 5 vms
                setCloudletList(createCloudlet(getBroker().getId(), 10, 100)); // creating 10 cloudlets

                broker.submitVmList(getVmList());
                broker.submitCloudletList(getCloudletList());

                CloudSim.resumeSimulation();

                break;

            default:
                Log.printLine(getName() + ": unknown event type");
                break;
        }
    }

    @Override
    public void startEntity() {
        Log.printLine(super.getName() + " is starting...");
        schedule(getId(), 200, CREATE_BROKER);
    }

    @Override
    public void shutdownEntity() {
    }

    public List<VmExt> getVmList() {
        return vmList;
    }

    protected void setVmList(List<VmExt> vmList) {
        this.vmList = vmList;
    }

    public List<CloudletExt> getCloudletList() {
        return cloudletList;
    }

    protected void setCloudletList(List<CloudletExt> cloudletList) {
        this.cloudletList = cloudletList;
    }

    public ArimaDatacenterBroker getBroker() {
        return broker;
    }

    protected void setBroker(ArimaDatacenterBroker broker) {
        this.broker = broker;
    }

    private static List<VmExt> createVM(int userId, int vms, int idShift) {
        //Creates a container to store VMs. This list is passed to the broker later
        LinkedList<VmExt> list = new LinkedList<VmExt>();

        //VM Parameters
        long size = 10000; //image size (MB)
        int ram = 512; //vm memory (MB)
        int mips = 250;
        long bw = 1000;
        int pesNumber = 1; //number of cpus
        String vmm = "Xen"; //VMM name

        //create VMs
        VmExt[] vm = new VmExt[vms];

        for (int i = 0; i < vms; i++) {
            vm[i] = new VmExt(idShift + i, userId, mips, pesNumber, ram, bw, size, vmm, new CloudletSchedulerTimeShared());
            list.add(vm[i]);
        }

        return list;
    }

    private static ArimaDatacenterBroker createBroker(String name) {

        ArimaDatacenterBroker broker = null;
        try {
            broker = new ArimaDatacenterBroker(name);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return broker;
    }
    
     private static List<CloudletExt> createCloudlet(int userId, int cloudlets, int idShift) {
        // Creates a container to store Cloudlets
        LinkedList<CloudletExt> list = new LinkedList<CloudletExt>();

        //cloudlet parameters
        long length = 40000;
        long fileSize = 300;
        long outputSize = 300;
        int pesNumber = 1;
        UtilizationModel utilizationModel = new UtilizationModelFull();

        CloudletExt[] cloudlet = new CloudletExt[cloudlets];

        for (int i = 0; i < cloudlets; i++) {
            cloudlet[i] = new CloudletExt(idShift + i, length, pesNumber, fileSize, outputSize, utilizationModel, utilizationModel, utilizationModel);
            // setting the owner of these Cloudlets
            cloudlet[i].setUserId(userId);
            list.add(cloudlet[i]);
        }

        return list;
    }

}
