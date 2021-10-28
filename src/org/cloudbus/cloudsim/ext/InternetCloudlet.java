/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.ext;


import java.util.HashMap;
import java.util.Map;

import org.cloudbus.cloudsim.Cloudlet;
import org.cloudbus.cloudsim.UtilizationModel;
import org.cloudbus.cloudsim.UtilizationModelFull;
import org.cloudbus.cloudsim.ext.datacenter.DatacenterController;
/**
 *
 * @author Admin
 */
public class InternetCloudlet extends Cloudlet {
	/** Any user specific or execution specific data can be passed along using this Map. */
	private Map data;
	private GeoLocatable originator;
	private int appId;
	private int parentId;
	private int requestCount;
		
	public InternetCloudlet(int cloudletID, 
							double cloudletLength,
							long cloudletFileSize, 
							long cloudletOutputSize, 
							GeoLocatable originator,
							int appId,
							int requestCount) {
                                   //UtilizationModel utilizationModel = ;
                                   //long cll =(new Double(cloudletLength)).longValue();
		super(cloudletID, (long) cloudletLength, 2, cloudletFileSize, cloudletOutputSize, new UtilizationModelFull(),new UtilizationModelFull(),new UtilizationModelFull());
		this.data = new HashMap();
		this.originator = originator; 
		this.appId = appId;
		this.requestCount = requestCount;
	}

	/**
	 * @return the data
	 */
	public Map getData() {
		return data;
	}
	
	public Object getData(String label){
		return data.get(label);
	}

	/**
	 * @param data the data to set
	 */
	public void addData(String label, Object data) {
		this.data.put(label, data);
	}
	
	public String toString(){
		return "cloudletid=" + super.getCloudletId() + ", data=" + data;
	}
	
	/**
	 * @return the originator
	 */
	public GeoLocatable getOriginator() {
		return originator;
	}

	/**
	 * @param originator the originator to set
	 */
	public void setOriginator(GeoLocatable originator) {
		this.originator = originator;
	}	

	
	public int getAppId(){
		return appId;
	}

	/**
	 * @return the parentId
	 */
	public int getParentId() {
		return parentId;
	}

	/**
	 * @param parentId the parentId to set
	 */
	public void setParentId(int parentId) {
		this.parentId = parentId;
	}

	/**
	 * @return the requestCount
	 */
	public int getRequestCount() {
		return requestCount;
	}	
}
