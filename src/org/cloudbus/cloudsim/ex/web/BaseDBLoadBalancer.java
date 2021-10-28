package org.cloudbus.cloudsim.ex.web;

import java.util.Arrays;
import java.util.List;

import org.cloudbus.cloudsim.ex.disk.HddVm;

/**
 * Implements common functionalities for DB load balancers
 * 
 * @author nikolay.grozev
 * 
 */
public abstract class BaseDBLoadBalancer implements IDBBalancer {

    protected List<HddVm> dbVms;

    /**
     * Constr.
     * 
     * @param dbVms
     *            - The list of DB vms to distribute cloudlets among.
     */
    public BaseDBLoadBalancer(final List<HddVm> dbVms) {
        this.dbVms = dbVms;
    }

    /**
     * Constr.
     * 
     * @param dbVms
     *            - The list of DB vms to distribute cloudlets among.
     */
    public BaseDBLoadBalancer(final HddVm... dbVms) {
        this(Arrays.asList(dbVms));
    }

    @Override
    public List<HddVm> getVMs() {
        return dbVms;
    }

    @Override
    public void setVms(final List<HddVm> vms) {
        dbVms = vms;
    }

}