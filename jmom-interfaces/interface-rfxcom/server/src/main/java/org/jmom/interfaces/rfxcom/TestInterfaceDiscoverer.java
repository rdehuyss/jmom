package org.jmom.interfaces.rfxcom;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.jmom.core.model.interfacing.HardwareDependency;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;

import java.util.Set;

import static org.jmom.core.model.interfacing.HardwareDependency.RXTX_COMM_PORT;

public class TestInterfaceDiscoverer extends AbstractExecutionThreadService implements InterfaceDiscoverer {

    private int count;
    private boolean error;

    public TestInterfaceDiscoverer(int count, boolean error) {
        this.count = count;
        this.error = error;
    }

    @Override
    protected void run() throws Exception {
        HardwareDependency.waitAndTakeLock(RXTX_COMM_PORT);

        System.out.print(toString() + ": Doing work...");
        for (int i = 0; i < count; i++) {
            Thread.sleep(1000);
            System.out.println("    " + toString() + ": finished item  " + i);

        }
        HardwareDependency.finishLock(RXTX_COMM_PORT);
        if (error) {
            throw new Exception("boe!");
        }
        System.out.println("Found rfxcom");
        super.triggerShutdown();
    }

    @Override
    protected void startUp() throws Exception {
        super.startUp();
        System.out.println(toString() + " Starting Discovery up");
    }

    @Override
    protected void shutDown() throws Exception {
        super.shutDown();
        System.out.println(toString() + " Shutting Discovery down");
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + count + ")";
    }

    public static void main(String[] args) throws InterruptedException {
        Set<Service> services = Sets.newHashSet(new TestInterfaceDiscoverer(3, false), new TestInterfaceDiscoverer(5, true), new TestInterfaceDiscoverer(10, false));
        ServiceManager serviceManager = new ServiceManager(services);
        serviceManager.addListener(new ServiceManager.Listener() {
            @Override
            public void healthy() {
                System.out.println("============== ServiceManager healthy");
            }

            @Override
            public void stopped() {
                System.out.println("============== ServiceManager stopped");
            }

            @Override
            public void failure(Service service) {
                System.out.println("Listener: failure");
            }
        });

        Stopwatch stopwatch = Stopwatch.createStarted();
        serviceManager.startAsync();

        System.out.println("Healthy: " + serviceManager.isHealthy());

        while (serviceManager.servicesByState().get(State.RUNNING).size() > 0) {
            Thread.sleep(5000);
            printStates(serviceManager);
        }
        stopwatch.stop();
        System.out.println("Discovery time took " + stopwatch.toString());
    }

    private static void printStates(ServiceManager serviceManager) {
        System.out.println("Service manager healthy: " + true + "; ");
        ImmutableMultimap<State, Service> stateServiceImmutableMultimap = serviceManager.servicesByState();
        ImmutableSet<State> states = stateServiceImmutableMultimap.keySet();
        for (State state : states) {
            ImmutableCollection<Service> services = stateServiceImmutableMultimap.get(state);
            System.out.println("State: " + state.name());
            for (Service service : services) {
                System.out.println("    " + ((TestInterfaceDiscoverer) service).toString());
            }
        }
    }

    @Override
    public String name() {
        return null;
    }
}
