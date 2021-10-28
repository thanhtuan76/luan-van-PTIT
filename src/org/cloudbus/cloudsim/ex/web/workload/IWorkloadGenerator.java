package org.cloudbus.cloudsim.ex.web.workload;

import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.ex.web.WebSession;

/**
 * Represents a timed factory for sessions of a given type. Defines the workload
 * consisting of sessions of a given type directed to a data center.
 * 
 * @author nikolay.grozev
 * 
 */
public interface IWorkloadGenerator {

    /**
     * Generates sessions for the period [startTime, startTime + periodLen].
     * 
     * @param startTime
     *            - the start time of the generated sessions.
     * @param periodLen
     *            - the length of the period.
     * @return a map between session start times and sessions.
     */
    public Map<Double, List<WebSession>> generateSessions(final double startTime, final double periodLen);

}
