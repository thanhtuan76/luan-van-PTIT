/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cloudbus.cloudsim.ex.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Level;
import org.cloudbus.cloudsim.core.CloudSim;

import org.cloudbus.cloudsim.ex.disk.HddResCloudlet;
import org.cloudbus.cloudsim.ex.disk.HddVm;
import org.cloudbus.cloudsim.ex.util.CustomLog;
import org.cloudbus.cloudsim.ex.web.workload.brokers.WebBroker;


public class ArimaWebLoadBalancer extends BaseWebLoadBalancer implements ILoadBalancer {

    private long startPositionWhenEqual = 0;
    private StringBuffer debugSB = new StringBuffer();
    WebBroker broker;

    /**
     * Constructor.
     * 
     * @param appId
     *            - the id of the applications, which this load balancer is
     *            serving.
     * @param ip
     *            - the IP address represented in a standard Ipv4 or IPv6 dot
     *            notation.
     * @param appServers
     *            - the application servers. Must not be null.
     * @param dbBalancer
     *            - the balancer of the DB cloudlets among DB servers. Must not
     *            be null.
     */
    public ArimaWebLoadBalancer(final long appId, final String ip, final List<HddVm> appServers,
            final IDBBalancer dbBalancer) {
        super(appId, ip, appServers, dbBalancer);
    }

    public ArimaWebLoadBalancer(final long appId, final String ip, final List<HddVm> appServers,
            final IDBBalancer dbBalancer, WebBroker broker) {
        super(appId, ip, appServers, dbBalancer);
        this.broker = broker;
    }

    @Override
    public void assignToServers(final WebSession... sessions) {
        // Filter all sessions without an assigned application server
        List<WebSession> noAppServSessions = new ArrayList<>();
        noAppServSessions.addAll(Arrays.asList(sessions));
        for (ListIterator<WebSession> iter = noAppServSessions.listIterator(); iter.hasNext();) {
            WebSession sess = iter.next();
            if (sess.getAppVmId() != null) {
                iter.remove();
            }
        }

        List<HddVm> runingVMs = getRunningAppServers();
        // No running AS servers - log an error
        if (runingVMs.isEmpty()) {
            for (WebSession session : noAppServSessions) {
                if (getAppServers().isEmpty()) {
                    CustomLog.printf(Level.SEVERE,
                            "[ %s ]: Arimas Load Balancer(%s): session %d cannot be scheduled, as there are no AS servers",CloudSim.clock(),
                            broker == null ? "N/A" : broker, session.getSessionId());
                } else {
                    CustomLog
                            .printf(Level.SEVERE,
                                    "[ %s ] : [Arimas Load Balancer](%s): session %d cannot be scheduled, as all AS servers are either booting or terminated",CloudSim.clock(),
                                    broker == null ? "N/A" : broker, session.getSessionId());
                }
            }
        } else {
            @SuppressWarnings("unchecked")
            Map<Integer, Integer> usedASServers = broker != null ? this.broker.getASServersToNumSessions()
                    : Collections.EMPTY_MAP;

            // Get the VMs which are utilized the least
            debugSB.setLength(0);
            List<HddVm> bestVms = new ArrayList<>();
            double bestUtilization = Double.MAX_VALUE;
            for (HddVm vm : runingVMs) {
                double vmUtilization = evaluateSuitability(vm);
                if (!vm.isOutOfMemory()) {
                    if (vmUtilization < bestUtilization) {
                        bestVms.clear();
                        bestUtilization = vmUtilization;
                        bestVms.add(vm);
                    } else if (vmUtilization == bestUtilization) {
                        bestVms.add(vm);
                    }
                }

                debugSB.append(String.format("%s[%s] cpu(%.2f), ram(%.2f), cdlts(%d), sess(%d); ", vm, vm.getStatus(),
                        vm.getCPUUtil(), vm.getRAMUtil(), vm.getCloudletScheduler().getCloudletExecList().size(),
                        !usedASServers.containsKey(vm.getId()) ? 0 : usedASServers.get(vm.getId())));
            }

            // Distribute the sessions among the best VMs
            long i = startPositionWhenEqual++;
            if (!bestVms.isEmpty()) {
                for (WebSession session : noAppServSessions) {
                    long index = i++ % bestVms.size();
                    HddVm hostVM = bestVms.get((int) index);
                    session.setAppVmId(hostVM.getId());

                    CustomLog
                            .printf("[ %s ]: [Arima Load Balancer(%s): Assigning sesssion %d to %s[%s] cpu(%.2f), ram(%.2f), cdlts(%d), sess(%d);", CloudSim.clock(),
                                    broker == null ? "N/A" : broker, session.getSessionId(), hostVM,
                                    hostVM.getStatus(), hostVM.getCPUUtil(), hostVM.getRAMUtil(), hostVM
                                            .getCloudletScheduler().getCloudletExecList().size(),
                                    !usedASServers.containsKey(hostVM.getId()) ? 0 : usedASServers.get(hostVM.getId()));
                    CustomLog.printf("[ %s ]: [Arima Load Balancer(%s), Candidate VMs: %s", CloudSim.clock(), broker == null ? "N/A" : broker,
                            debugSB);
                }
            }

        }

        // Set the DB VM
        for (WebSession session : sessions) {
            if (session.getDbBalancer() == null) {
                session.setDbBalancer(getDbBalancer());
            }
        }

        // Log the state of the DB servers
        debugSB.setLength(0);
        for (HddVm dbVm : getDbBalancer().getVMs()) {
            debugSB.append(String.format("%s cpu(%.2f), ram(%.2f), disk(%.2f), cdlts(%d);", dbVm, dbVm.getCPUUtil(),
                    dbVm.getRAMUtil(), dbVm.getDiskUtil(), dbVm.getCloudletScheduler().getCloudletExecList().size()));
        }
        CustomLog.printf("[ %s ]:  [Arima Load Balancer], DB VMs: %s", CloudSim.clock(), debugSB);

    }

    protected static double evaluateSuitability(final HddVm vm) {
        double sumExecCloudLets = 0;
        for (HddResCloudlet cloudlet : vm.getCloudletScheduler().<HddResCloudlet> getCloudletExecList()) {
            sumExecCloudLets += cloudlet.getCloudletLength();
        }
        double vmMips = vm.getMips() * vm.getNumberOfPes();
        return sumExecCloudLets / vmMips;
    }
}

