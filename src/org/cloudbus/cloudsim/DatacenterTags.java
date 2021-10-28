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
public class DatacenterTags {
	
	 //starting constant values for VM-related tags
    private static final int VMBASE = 1000;

    /**
     * Denotes a request to create a new VM in a Datacentre
     * With acknowledgement information sent by the Datacentre
     */
    public static final int VM_CREATE = VMBASE + 1;
    
    /**
     * Denotes a request to create a new VM in a Datacentre
     * With acknowledgement information sent by the Datacentre
     */
    public static final int VM_CREATE_ACK = VMBASE + 2;
    
    /**
     * Denotes a request to destroy a new VM in a Datacentre
     */
    public static final int VM_DESTROY = VMBASE + 3;

    /**
     * Denotes a request to destroy a new VM in a Datacentre
     */
    public static final int VM_DESTROY_ACK = VMBASE + 4;
    
    /**
     * Denotes a request to migrate a new VM in a Datacentre
     */
    public static final int VM_MIGRATE = VMBASE + 5;
    
    /**
     * Denotes a request to migrate a new VM in a Datacentre
     * With acknowledgement information sent by the Datacentre
     */
    public static final int VM_MIGRATE_ACK = VMBASE + 6;
    
 
    /**
     * Denotes an event to send a file from a user to a datacenter
     */
    public static final int VM_DATA_ADD = VMBASE + 7;
    
    /**
     * Denotes an event to send a file from a user to a datacenter
     */
    public static final int VM_DATA_ADD_ACK = VMBASE + 8;
    
    /**
     * Denotes an event to remove a file from a datacenter
     */
    public static final int VM_DATA_DEL = VMBASE + 9;
    
    /**
     * Denotes an event to remove a file from a datacenter
     */
    public static final int VM_DATA_DEL_ACK = VMBASE + 10;
    
    /**
     * Denotes an internal event generated in a Datacenter
     */
    public static final int VM_DATACENTER_EVENT = VMBASE + 21;
    
    /**
     * Denotes an internal event generated in a Broker
     */
    public static final int VM_BROKER_EVENT = VMBASE + 22;
        
	protected DatacenterTags(){
		
	}
	
}
