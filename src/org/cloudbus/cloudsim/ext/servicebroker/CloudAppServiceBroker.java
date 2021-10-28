/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.ext.servicebroker;

import org.cloudbus.cloudsim.ext.GeoLocatable;

/**
 *
 * @author Admin
 */
public interface CloudAppServiceBroker {

	String getDestination(GeoLocatable inquirer);
}
