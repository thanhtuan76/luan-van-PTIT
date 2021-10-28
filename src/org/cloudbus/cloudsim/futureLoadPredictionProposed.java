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
public class futureLoadPredictionProposed extends DatacenterBroker {

    private SimEvent evbuf; //Bộ đệm cho các sự kiện đến đã chọn. Luu deferred queue
    private int state;	//Trạng thái của các thực thể 
    private boolean createdVm = false; //Máy ảo đã được tạo?
    private boolean allCloudletSubmited = false; //Tat ca cloudlet da duoc gui? 
    public Map<Integer, Integer> vmIndexTable; //Load table, Luu VMId va so cloudlet dang nhan de xu ly
    private Map<Integer, Integer> submitCloudlettoVm;
    public Map<Integer, Integer> vmQueueTemp;
    public Map<Integer, Integer> vmQueuePer;
    public int allocatedType = 0;

    public futureLoadPredictionProposed(String name, int allocatedCurr) throws Exception {
        super(name); //super() được sử dụng để triệu hồi Constructor của lớp cha (lớp cơ sở) gần nhất, ở đây lớp cha là DatacenterBroker

        allocatedType = allocatedCurr;
        vmIndexTable = new HashMap<Integer, Integer>();
        submitCloudlettoVm = new HashMap<Integer, Integer>();

    }

    //Su dung o method run()
    public boolean getStatusCreateVm() {
        return createdVm;
    }

    //processEvent() is invoked by the Simulation class whenever there is an event in the deferred queue, which needs to be processed by the entity.
    //Xu ly cac su kien tra ve
    @Override
    public void processEvent(SimEvent ev) {
        //Lớp SimEvent mô tả một sự kiện mô phỏng được truyền giữa các thực thể trong mô phỏng.
        switch (ev.getTag()) { //Get the user-defined tag of this event.
            case CloudSimTags.RESOURCE_CHARACTERISTICS_REQUEST: //Biểu thị yêu cầu về thông tin về các đặc tính tài nguyên lưới.
                processResourceCharacteristicsRequest(ev); //Process a request for the characteristics of a PowerDatacenter. Class NetDatacenterBroker
                break;
            case CloudSimTags.RESOURCE_CHARACTERISTICS: //Biểu thị thông tin về các đặc tính tài nguyên lưới. Thẻ này thường được sử dụng giữa CloudSim và thực thể CloudResource.
                processResourceCharacteristics(ev); //Process the return of a request for the characteristics of a PowerDatacenter. Class NetDatacenterBroker
                break;
            case CloudSimTags.VM_CREATE_ACK: //Biểu thị yêu cầu tạo máy ảo mới trong Datacenter Với thông tin xác nhận được gửi bởi Datacenter
                processVmCreate(ev); //Process the ack received due to a request for VM creation. Class PowerDatacenterBroker
                break;
            case CloudSimTags.CLOUDLET_RETURN: //Denotes the return of a Cloudlet back to sender.
                Cloudlet cloudlet = (Cloudlet) ev.getData();
                deAllocatedVm(cloudlet.getVmId()); //xem phuong thuc ben duoi
                deAllocatedCloudlet(cloudlet.getCloudletId());
                processCloudletReturn(ev); //Class DatacenterBroker. Process a cloudlet return event.
                break;
            case CloudSimTags.END_OF_SIMULATION: //Denotes the end of simulation
                shutdownEntity(); //Class CloudSimShutdown. This method is invoked by the Simulation before the simulation finishes.
                break;
            default:
                processOtherEvent(ev); //Class CloudInformationService. This method needs to override by a child class for processing other events.
                break;
        }
    }

    @Override
    public void run() {
        SimEvent ev = evbuf != null ? evbuf : getNextEvent(); //Class SimEntity. Get the first event waiting in the entity's deferred queue, or if there are none, wait for an event to arrive.
        while (ev != null) {
            processEvent(ev); //Phuong thuc o tren
            // Nếu tất cả VM đã được khởi tạo, đệ trình cloudlet
            if (getStatusCreateVm() && !allCloudletSubmited) {
                submitCloudlets_2();
            }
            if (state != RUNNABLE) {
                break;
            }
            // Nhận sự kiện tiếp theo trong deffer queue 
            ev = getNextEvent();
            //Get the first event waiting in the entity's deferred queue, or if there are none, wait for an event to arrive.
        }
        evbuf = null;
    }

    @Override
    public void submitVmList(List<? extends Vm> list) {
        getVmList().addAll(list);
        for (int i = 0; i < list.size(); i++) {
            Vm vm = list.get(i);
            vmIndexTable.put(vm.getId(), i);
        }

    }

    //Phan bo them mot cloudlet cho Vm
    @SuppressWarnings("unused")
    public void allocatedVm(int currVm) {
        Integer currCount = getLoadVm(currVm); //Trả về value tương ứng với key (currVm) trong hashmap

        if (vmIndexTable.isEmpty()) { //Ch
            currCount = 0;
        }
        if (currCount == null) {
            currCount = 0;
        }
        currCount = currCount + 1;
        vmIndexTable.put(currVm, currCount); //Thêm giá một cặp key, value vào hashmap

        Log.printLine(geLoadFactorCurr(currVm, CloudSim.clock()) + "Tải VM# " + currVm + " khi cấp phát cloudlet đến là " + getLoadVm(currVm));
    }

    //Huy mot cloudlet da hoan thanh cho Vm
    @SuppressWarnings("unused")
    public void deAllocatedVm(int currVm) {
        Integer currCount = getLoadVm(currVm);

        if (vmIndexTable.isEmpty()) { //Ch
            currCount = 0;
        }
        if (currCount == null) {
            currCount = 0;
        }
        currCount = currCount - 1;
        vmIndexTable.put(currVm, currCount);

        Log.printLine(geLoadFactorCurr(currVm, CloudSim.clock()) + "Tải VM# " + currVm + " khi cloudlet đã thực thi là: " + getLoadVm(currVm));
    }

    //Ch, Them <cloudletID, vmId>, quan he <1, n>
    protected void allocatedCloudlet(int currCloudletId, int currVmId) {
        submitCloudlettoVm.put(currCloudletId, currVmId);
    }

    //Ch, Xoa tat ca <cloudletID, vmId> co gia tri la vmId truyen vao
    protected void deAllocatedCloudlet(int currCloudletId) {
        if (!vmIndexTable.isEmpty()) {
            //for (Map.Entry<Integer, Integer> entry : vmIndexTable.entrySet()) {
            //if (entry.getValue() == currVmId) {
            //submitCloudlettoVm.remove(entry.getKey());
            submitCloudlettoVm.remove(currCloudletId);
            //}
            //}
        }
    }

    //Tìm id máy ảo có trong danh sách máy ảo đã được tạo ko? 
    public int findVmId(int vmIndex) {
        int vmId = -1;

        for (int thisVmid = 0; thisVmid < getVmsCreatedList().size(); thisVmid++) {
            //Class DatacenterBroker. Gets the vm list.
            if (getVmsCreatedList().get(thisVmid).getId() == vmIndex) {
                vmId = thisVmid;
                break;
            }
        }
        return vmId;
    }

    //Lay so cloudlet da duoc cap phat cho idVm
    public int getLoadVm(int currentVm) {
        int load = 0;
        if (vmIndexTable.containsKey(currentVm)) { //Ch
            load = vmIndexTable.get(currentVm);
        }
        return load; //Trả về value tương ứng với key (currentVm) trong hashmap
    }

    //Ch Lay so cloudlet da duoc cap phat cho idVm hien tai
    protected int getcurrentLoadVm(int vmId) {
        return vmIndexTable.get(vmId);
    }

    //Space-Space
    protected double getCapacity(Vm vm) {
        double capacity = 0.0;
        int cpus = 0;

        for (Double mips : vm.getCloudletScheduler().getCurrentMipsShare()) {
            capacity += mips;
            if (mips > 0.0) {
                cpus++;
            }
        }
        capacity = capacity / cpus;
        return capacity;
    }

    //Space-time
    protected double getCapacity2(Vm vm) {
        double capacity = 0.0;
        int cpus = 0;
        int nCoreOfCloudlet = 0;

        for (Double mips : vm.getCloudletScheduler().getCurrentMipsShare()) {
            capacity += mips;
            if (mips > 0.0) {
                cpus++;
            }
        }
        nCoreOfCloudlet = getLoadVm(vm.getId()) * 2;

        if (cpus < nCoreOfCloudlet) {
            cpus = nCoreOfCloudlet;
        }
        capacity = capacity / cpus;
        return capacity;
    }

    //Time-Space
    protected double getCapacity3(Vm vm) {
        double capacity = 0.0;
        int cpus = 0;
        int nCoreOfCloudlet = 0;

        for (Double mips : vm.getCloudletScheduler().getCurrentMipsShare()) {
            capacity += mips;
            if (mips > 0.0) {
                cpus++;
            }
        }
        nCoreOfCloudlet = vm.getCloudletScheduler().runningCloudlets() * 2;
        if (cpus < nCoreOfCloudlet) {
            cpus = nCoreOfCloudlet;
        }
        capacity = capacity / cpus;
        return capacity;
    }

    //Time-Time
    protected double getCapacity4(Vm vm) {
        double capacity = 0.0;
        int cpus = 0;
        int nCoreOfCloudlet = 0;

        for (Double mips : vm.getCloudletScheduler().getCurrentMipsShare()) {
            capacity += mips;
            if (mips > 0.0) {
                cpus++;
            }
        }
        for (Double mips : vm.getCloudletScheduler().getCurrentRequestedMips()) {
            if (mips > 0.0) {
                nCoreOfCloudlet++;
            }
        }
        nCoreOfCloudlet = getLoadVm(vm.getId()) * 2;
        if (cpus < nCoreOfCloudlet) {
            cpus = nCoreOfCloudlet;
        }
        capacity = capacity / cpus;
        return capacity;
    }

    public double geLoadFactorCurr(int currVm, double time) {
        double totalUtilization = 0;
        Vm vm = getVmsCreatedList().get(currVm);
        totalUtilization = 2 - vm.getTotalUtilizationOfCpu(time);
        //return vm.getTotalUtilizationOfCpuMips(time);
        return totalUtilization;
    }

    public double gePerformanceFactorCurr(List<Cloudlet> list) {

        double time_actual = 0;
        int size = list.size();
        Cloudlet cloudlet;
        for (int i = 0; i < size; i++) {
            cloudlet = list.get(i);
            if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
                time_actual = time_actual + cloudlet.getActualCPUTime();
            }
        }
        return time_actual / size;
    }
    //Tim Vm voi load (so cloudlet) nho nhat trong vmIndexTable va tang so cloudlet cap phat cho vm tim duoc

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
        allocatedVm(vmId); //remove by Ch
        return vmId;
    }
    
    //Tim Vm voi load (so cloudlet) nho nhat trong vmIndexTable va tang so cloudlet cap phat cho vm tim duoc
    public int getNextAvailableVm1(double cloudletExecStartTime, long cloudletLength, int cloudletNumberOfPes, double avgSize) {
        int vmId = -1;
        long currCount;
        long minCount = Integer.MAX_VALUE;
        long nextCloudletLength = 0;

        for (int thisVmId : vmIndexTable.keySet()) {
            currCount = get_TotalCloudletLength(thisVmId);
            nextCloudletLength = get_NextCloudletLength(thisVmId, cloudletExecStartTime, cloudletLength, cloudletNumberOfPes, avgSize) ;
            currCount += nextCloudletLength;
            if (currCount < minCount) {
                minCount = currCount;
                vmId = thisVmId;
            }
        }
        return vmId;
    }

    public long get_TotalCloudletLength(int currVmId) {
        long cloudletLength = 0;
        long currCloudletLength = 0;
        Vm vm;
        Cloudlet cloudlet;
        if (!submitCloudlettoVm.isEmpty()) {
            for (Map.Entry<Integer, Integer> entry : submitCloudlettoVm.entrySet()) {
                if (entry.getValue() == currVmId) {
                    cloudlet = getCloudletSubmittedList().get(entry.getKey());
                    vm = getVmsCreatedList().get(currVmId);
                    if (cloudlet != null) {
                        if (cloudlet.getCloudletStatusString() == "Created" || cloudlet.getCloudletStatusString() == "Queued" || cloudlet.getCloudletStatusString() == "Ready") {
                            if (allocatedType == 1) {
                                currCloudletLength = (long) (cloudlet.getExecStartTime() + (cloudlet.getCloudletLength() / (getCapacity(vm) * cloudlet.getNumberOfPes())));
                            } else if (allocatedType == 2) {
                                currCloudletLength = (long) (cloudlet.getExecStartTime() + (cloudlet.getCloudletLength() / (getCapacity2(vm) * cloudlet.getNumberOfPes())));
                            } else if (allocatedType == 3) {
                                currCloudletLength = (long) (cloudlet.getExecStartTime() + (cloudlet.getCloudletLength() / (getCapacity3(vm) * cloudlet.getNumberOfPes())));
                            } else if (allocatedType == 4) {
                                currCloudletLength = (long) (cloudlet.getExecStartTime() + (cloudlet.getCloudletLength() / (getCapacity4(vm) * cloudlet.getNumberOfPes())));
                            }
                        }
                    }
                    cloudletLength = cloudletLength + currCloudletLength;
                    //Log.printLine("Cloudlet status: " + cloudlet.getCloudletStatusString() + " (" + cloudletLength + ")");				
                }
            }
        }
        return cloudletLength;
    }

    public long get_NextCloudletLength(int currVmId, double cloudletExecStartTime, long cloudletLength, int cloudletNumberOfPes, double avgSize) {
        long nextCloudletLength = 0;
        long currCloudletLength = 0;
        Vm vm;
        vm = getVmsCreatedList().get(currVmId);
        if (allocatedType == 1) {
            currCloudletLength = (long) (cloudletExecStartTime + (cloudletLength / (getCapacity(vm) * cloudletNumberOfPes)));
        } else if (allocatedType == 2) {
            currCloudletLength = (long) (cloudletExecStartTime + (cloudletLength / (getCapacity2(vm) * cloudletNumberOfPes)));
        } else if (allocatedType == 3) {
            currCloudletLength = (long) (cloudletExecStartTime + (cloudletLength / (getCapacity3(vm) * cloudletNumberOfPes)));
        } else if (allocatedType == 4) {
            currCloudletLength = (long) (cloudletExecStartTime + (cloudletLength / (getCapacity4(vm) * cloudletNumberOfPes)));
        }
        nextCloudletLength =  getAlpha(cloudletLength, avgSize) + currCloudletLength;
        return nextCloudletLength;
    }

    @Override
    public void submitCloudlets() {
        int countCloudlets = 0;
        int vmIndex;

        for (Cloudlet cloudlet : getCloudletList()) { //Gets the cloudlet list.

            vmIndex = getNextAvailableVm1(cloudlet.getExecStartTime(), cloudlet.getCloudletLength(), cloudlet.getNumberOfPes(), cloudlet.getAverageSize()); ////May ao ke tiep toi uu duoc chon

            //Tim Vm voi load nho nhat trong vmIndexTable va tang cap phat cloudlet cho vm
            Vm vm;
            if (vmIndex == -1) {
                vmIndex = 0;
            }
            vm = getVmsCreatedList().get(vmIndex);

            Log.printLine(CloudSim.clock() + ": " + getName() + ": Đang gởi cloudlet " + cloudlet.getCloudletId() + " (" + cloudlet.getCloudletTotalLength() + " x " + cloudlet.getNumberOfPes() + ") tới máy ảo VM #" + vm.getId());
            cloudlet.setVmId(vm.getId()); //gui cloudlet toi vm

            allocatedVm(vmIndex); //Ch
            allocatedCloudlet(cloudlet.getCloudletId(), vmIndex);

            countCloudlets++;

            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            //(Class SimEntity) Sends an event/message to another entity by delaying the simulation time from the current time, with a tag representing the event type.

            cloudletsSubmitted++; //a property of class datacenter broker
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
        //Ban dau chua co cloudlet nao       
        if (getCloudletList().size() != 0) {
            Cloudlet cloudlet = getCloudletList().get(0);
            Vm vm;
            int vmIndex = getNextAvailableVm1(cloudlet.getExecStartTime(), cloudlet.getCloudletLength(), cloudlet.getNumberOfPes(), cloudlet.getAverageSize()); ////May ao ke tiep toi uu duoc chon
            if (cloudlet.getVmId() == -1) {
                int index = findVmId(vmIndex);
                vm = getVmsCreatedList().get(index);
            } else {
                 vm = VmList.getById(getVmsCreatedList(), cloudlet.getVmId());
                  if (vm == null) {
                    // vm was not created
                    Log.printLine(CloudSim.clock() + ": " + getName() + ": Postponing execution of cloudlet " + cloudlet.getCloudletId() + ": bount VM not available");
                }
            }

            Log.printLine(CloudSim.clock() + ": " + getName() + ": Đang gởi cloudlet " + cloudlet.getCloudletId() + " (" + cloudlet.getNumberOfPes() + " x " + cloudlet.getCloudletLength() + ") tới máy ảo VM #" + vm.getId());
            cloudlet.setVmId(vm.getId());

            allocatedVm(vmIndex); //Ch
            allocatedCloudlet(cloudlet.getCloudletId(), vmIndex);

            sendNow(getVmsToDatacentersMap().get(vm.getId()), CloudSimTags.CLOUDLET_SUBMIT, cloudlet);
            cloudletsSubmitted++; //a property of class datacenter broker
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
                    // if some vm were created
                    submitCloudlets();
                    Log.printLine("Some vm were created");
                } else {
                    // no vms created. abort
                    Log.printLine(CloudSim.clock() + ": " + getName() + ": none of the required VMs could be created. Aborting");
                    finishExecution();
                }
            }
        }
    }

    //Ch
    public void print_vmIndexTable() {
        if (vmIndexTable.isEmpty()) {
            System.out.println("vmIndexTable is empty.");
        } else {
            for (Map.Entry<Integer, Integer> entry : vmIndexTable.entrySet()) {
                System.out.println("vmIndexTable: Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }
    }

    //Ch
    public void print_submitCloudlettoVm() {
        if (submitCloudlettoVm.isEmpty()) {
            System.out.println("submitCloudlettoVm is empty.");
        } else {
            for (Map.Entry<Integer, Integer> entry : submitCloudlettoVm.entrySet()) {
                System.out.println("submitCloudlettoVm: Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }
    }
    
    public long getAlpha(long cloudletLength, double avgSize)
    {
        long a =0;
        a = (long) (cloudletLength/avgSize);
        return a;
    }
}
