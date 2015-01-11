package org.jmom.core.services.interfacing;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.model.eda.events.InterfaceDiscoveryStartedEvent;
import org.jmom.core.model.eda.events.InterfaceDiscoveryFinishedEvent;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;

import java.util.Set;

public class InterfaceDiscoveryManager extends ServiceManager.Listener implements JMomBusAware {

    private JMomBus jMomBus;
    private Set<InterfaceDiscoverer> interfaceDiscoverers;
    private ServiceManager serviceManager;

    public InterfaceDiscoveryManager(JMomBus jMomBus, Set<InterfaceDiscoverer> interfaceDiscoverers) {
        this.jMomBus = jMomBus;
        this.interfaceDiscoverers = interfaceDiscoverers;
    }

    public void startDiscovery() {
        jMomBus.post(new InterfaceDiscoveryStartedEvent());
        serviceManager = new ServiceManager(interfaceDiscoverers);
        serviceManager.addListener(this);
        serviceManager.startAsync();
    }

    public void destroy() {
        if(serviceManager.isHealthy()) {
            serviceManager.stopAsync();
        }
    }

    @Override
    public void stopped() {
        super.stopped();
        jMomBus.post(new InterfaceDiscoveryFinishedEvent());
    }

    @Override
    public void failure(Service service) {
        super.failure(service);
    }
}
