/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.DatacenterBroker;
import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.core.CloudSimTags;
import org.cloudbus.cloudsim.core.SimEvent;
import org.cloudbus.cloudsim.lists.VmList;

/**
 *
 * @author Admin
 */
public class activeVmOrigin extends DatacenterBroker {

    private SimEvent evbuf;
    private int state;
    private boolean createdVm = false;
    private boolean allCloudletSubmited = false;
    public Map<Integer, Integer> vmIndexTable;

    public activeVmOrigin(String name) throws Exception {
        super(name);
        vmIndexTable = new HashMap<Integer, Integer>();
    }

    public boolean getStatusCreateVm() {
        return createdVm;
    }

    @Override
    public void processEvent(SimEvent ev) {
        switch (ev.getTag()) {
            case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST:
                processResourceCharacteristicsRequest(ev);
                break;
            case CloudSimTags.RESOURCE_CHARACTERISTICS:
                processResourceCharacteristics(ev);
                break;
            case CloudSimTags.VM_CREATE_ACK:
                processVmCreate(ev);
                break;
            case CloudSimTags.CLOUDLET_RETURN:
                processCloudletReturn(ev);
                break;
            case CloudSimTags.END_OF_SIMULATION:
                shutdownEntity();
                break;
            default:
                processOtherEvent(ev);
                break;
        }
    }

    @Override
    public void run() {
        SimEvent ev = evbuf != null ? evbuf : getNextEvent();
        while (ev != null) {
            processEvent(ev);
            if (getStatusCreateVm() && !allCloudletSubmited) {
                submitCloudlets_2();
            }
            if (state != RUNNABLE) {
                break;
            }
            ev = getNextEvent();
        }
        evbuf = null;
    }

    @Override
    public void submitVmList(List<? extends Vm> list) {
        getVmList().addAll(list);
        for(int i=0; i<list.size(); i++)
        {
            Vm vm = list.get(i);
            vmIndexTable.put(vm.getId(), i);
        }
        
    }

    @SuppressWarnings("unused")
    public void allocatedVm(int currVm) {
        Integer currCount = getLoadVm(currVm);
        if (vmIndexTable.isEmpty()) {
            currCount = 0;
        }
        if (currCount == null) {
            currCount = 0;
        }
        currCount = currCount + 1;
        vmIndexTable.put(currVm, currCount);
        Log.printLine("Tải VM# " + currVm + " khi cấp phát cloudlet đến là " + getLoadVm(currVm));
    }

    @SuppressWarnings("unused")
    public void deAllocatedVm(int currVm) {
        Integer currCount = getLoadVm(currVm);
        if (vmIndexTable.isEmpty()) {
            currCount = 0;
        }
        if (currCount == null) {
            currCount = 0;
        }
        currCount = currCount - 1;
        vmIndexTable.put(currVm, currCount);
        Log.printLine("Tải VM# " + currVm + " khi cloudlet đã thực thi là: " + getLoadVm(currVm));
    }

    public int findVmId(int vmIndex) {
        int vmId = -1;
        for (int thisVmid = 0; thisVmid < getVmsCreatedList().size(); thisVmid++) {
            if (getVmsCreatedList().get(thisVmid).getId() == vmIndex) {
                vmId = thisVmid;
                break;
            }
        }
        return vmId;
    }

    public int getLoadVm(int currentVm) {
        return vmIndexTable.get(currentVm);
    }

    public int getNextAvailableVm() {
        int vmId = -1;
        int currCount;
        int minCount = Integer.MAX_VALUE;
        for (int thisVmId : vmIndexTable.keySet()) {
            currCount = getLoadVm(thisVmId);
            if (currCount < minCount) {
                minCount = currCount;
                vmId = thisVmId;
            }
        }
        allocatedVm(vmId);
        return vmId;
    }

    @Override
    public void submitCloudlets() {
        int countCloudlets = 0;
        int vmIndex;
        for (Cloudlet cloudlet : getCloudletList()) {
            vmIndex = getNextAvailableVm();
            Vm vm;
            if (cloudlet.getVmId() == -1) {
                int index = findVmId(vmIndex);
                vm = getVmsCreatedList().get(index);
            } else {
                vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
                if (vm == null) {
                    Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet " + cloudlet.getCloudletId() + ": bount VM not available");
                    continue;
                }
            }
            Log.printLine(CloudSim.clock() + ": " + getName() + ": Đang gởi cloudlet " + cloudlet.getCloudletId() + " tới máy ảo VM #" + vm.getId());
            cloudlet.setVmId(vm.getId());
            countCloudlets++;
            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletsSubmitted++;
            getCloudletSubmittedList().add(cloudlet);
            if (countCloudlets == 35) {
                break;
            }
        }
        for (Cloudlet cloudlet : getCloudletSubmittedList()) {
            getCloudletList().remove(cloudlet);
        }
    }

    public void submitCloudlets_2() {
        int vmIndex = getNextAvailableVm();
        if (getCloudletList().size() != 0) {
            Cloudlet cloudlet = getCloudletList().get(0);
            Vm vm;
            if (cloudlet.getVmId() == -1) {
                int index = findVmId(vmIndex);
                vm = getVmsCreatedList().get(index);
            } else {
                vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
                if (vm == null) {
                    Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet " + cloudlet.getCloudletId() + ": bount VM not available");
                }
            }
            Log.printLine(CloudSim.clock() + ": " + getName() + ": Đang gởi cloudlet " + cloudlet.getCloudletId() + " tới máy ảo VM #" + vm.getId());
            cloudlet.setVmId(vm.getId());
            sendNow(getVmsToDatacentersMap().get(vm.getId()),
                    CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletsSubmitted++;
            getCloudletSubmittedList().add(cloudlet);
            getCloudletList().remove(cloudlet);
        } else {
            allCloudletSubmited = true;
        }
    }

    @Override
    public void processVmCreate(SimEvent ev) {
        int[] data = (int[]) ev.getData();
        int datacenterId = data[0];
        int vmId = data[1];
        int result = data[2];
        if (result == CloudSimTags.TRUE) {
            getVmsToDatacentersMap().put(vmId, datacenterId);
            getVmsCreatedList().add(VmList.getById(getVmList(), vmId));
            Log.printLine(CloudSim.clock() + ": " + getName() + ": VM #" + vmId + " has been created in Datacenter #" + datacenterId + ", Host #" + VmList.getById(getVmsCreatedList(), vmId).getHost().getId());
        } else {
            Log.printLine(CloudSim.clock() + ": " + getName() + ": Creation of VM #" + vmId + " failed in Datacenter #" + datacenterId);
        }
        incrementVmsAcks();
        if (getVmsCreatedList().size() == getVmList().size() - getVmsDestroyed()) {
            Log.printLine("Tất cả yêu cầu máy ảo đã được khởi tạo.");
            createdVm = true;
            submitCloudlets();
        } else {
            if (getVmsRequested() == getVmsAcks()) {
                for (int nextDatacenterId : getDatacenterIdsList()) {
                    if (!getDatacenterRequestedIdsList().contains(nextDatacenterId)) {
                        createVmsInDatacenter(nextDatacenterId);
                        return;
                    }
                }
                if (getVmsCreatedList().size() > 0) {
                    submitCloudlets();
                    Log.printLine("Some vm were created");
                } else {
                    Log.printLine(CloudSim.clock() + ": " + getName() + ": none of the required VMs could be created. Aborting");
                    finishExecution();
                }
            }
        }
    }
}
