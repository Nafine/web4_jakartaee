package se.ifmo.mbean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Initialized;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

@ApplicationScoped
public class MBeanRegistrar {

    @Inject
    private DotCounterService counterService;

    private ObjectName pointStatsName;
    private ObjectName missRatioName;

    void onStart(@Observes @Initialized(ApplicationScoped.class) Object event) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            pointStatsName = new ObjectName("se.ifmo:type=PointStats");
            missRatioName = new ObjectName("se.ifmo:type=MissRatio");

            if (!mbs.isRegistered(pointStatsName)) {
                mbs.registerMBean(new PointStats(counterService), pointStatsName);
            }
            if (!mbs.isRegistered(missRatioName)) {
                mbs.registerMBean(new MissRatio(counterService), missRatioName);
            }
        } catch (Exception e) {
            throw new RuntimeException("MBean registration failed", e);
        }
    }

    void onStop(@Observes @BeforeDestroyed(ApplicationScoped.class) Object event) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            if (pointStatsName != null && mbs.isRegistered(pointStatsName)) {
                mbs.unregisterMBean(pointStatsName);
            }
            if (missRatioName != null && mbs.isRegistered(missRatioName)) {
                mbs.unregisterMBean(missRatioName);
            }
        } catch (Exception ignored) {
        }
    }
}
