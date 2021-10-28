/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim;

/**
 *
 * @author Admin
 */
public class VirtualMachine {
    protected VMCharacteristics characteristics;
	protected Host host;
	
	/**
	 * Creates a new VM object
	 * @param characteristics description of the VM
	 * @pre characteristics != $null
	 * @post $none
	 */
	public VirtualMachine(VMCharacteristics characteristics) {
		this.characteristics = characteristics;
	}

	/**
	 * Gets the ID of the VM 
	 * @return Vm ID
	 * @pre $none
	 * @post $none
	 */
	public int getVmId() {
		return characteristics.getVmId();
	}
	
	/**
	 * Gets the ID of the owner of the VM 
	 * @return VM's owner ID
	 * @pre $none
	 * @post $none
	 */
	public int getUserId() {
		return characteristics.getUserId();
	}
	
	/**
	 * Gets the number of CPUs required by the VM
	 * @return number of CPUs
	 * @pre $none
	 * @post $none
	 */
	public int getCpus() {
		return characteristics.getCpus();
	}

	/**
	 * Sets the number of CPUS required by this VM
	 * @param cpus number of CPUs
	 * @pre cpus > 0
	 * @post $none
	 */
	public void setCpus(int cpus) {
		characteristics.setCpus(cpus);
	}

	/**
	 * Gets the amount of memory used by this VM
	 * @return amount of memory used by the VM
	 * @pre $none
	 * @post $none
	 */
	public int getMemory() {
		return characteristics.getMemory();
	}

	/**
	 * Sets the amount of memory used by this VM
	 * @param memory new amount of memory
	 * @pre memory > 0
	 * @post $none
	 */
	public void setMemory(int memory) {
		characteristics.setMemory(memory);
	}
	
	/**
	 * Gets the amount of storage used by this VM
	 * @return amount of storage used by the VM
	 * @pre $none
	 * @post $none
	 */
	public long getSize() {
		return characteristics.getSize();
	}
	
	/**
	 * Sets the amount of storage used by this VM
	 * @param size new amount of storage
	 * @pre size > 0
	 * @post $none
	 */
	public void setSize(long size) {
		characteristics.setSize(size);
	}
	
	/**
	 * Gets the amount of bandwidth used by this VM
	 * @return amount of bandwidth used by the VM
	 * @pre $none
	 * @post $none
	 */
	public long getBw() {
		return characteristics.getBw();
	}

	/**
	 * Sets the amount of bandwidth used by this VM
	 * @param bw new amount of bandwidth
	 * @pre bw > 0
	 * @post $none
	 */
	public void setBw(long bw) {
		characteristics.setBw(bw);
	}
	
	/**
	 * Gets the priority of this VM
	 * @return priority of the VM
	 * @pre $none
	 * @post $none
	 */
	public int getPriority() {
		return characteristics.getPriority();
	}

	/**
	 * Sets the priority of this VM.
	 * @param priority priority of this VM. The meaning of
	 *        this value depends on the scheduling policy
	 *        of the host.
	 * @pre priority > 0
	 * @post $none
	 */
	public void setPriority(int priority) {
		characteristics.setPriority(priority);
	}
	
	/**
	 * Sets the host that runs this VM
	 * @param host Host running the VM
	 * @pre host != $null
	 * @post $none
	 */
	public void setHost(Host host){
		this.host=host;
	}
	
	/**
	 * Returns an object of the type VMCharacteristics, with
	 * the description of this VM
	 * @return characteristics of this VM
	 * @pre $none
	 * @post $none
	 */
	public VMCharacteristics getCharacteristics(){
		return this.characteristics;
	}
	
	/**
	 * Returns a reference to the scheduler in use by the VM
	 * @return scheduler in use by the VM
	 * @pre $none
	 * @post $none
	 */
	public VmScheduler getVMScheduler(){
		return this.characteristics.getVMScheduler();
	}
}
