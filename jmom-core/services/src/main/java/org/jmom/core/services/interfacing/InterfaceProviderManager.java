package org.jmom.core.services.interfacing;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import org.jmom.core.model.configuration.CentralControlUnitConfiguration;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.interfacing.InterfaceProvider;

import java.util.Set;

import static com.google.common.collect.JMomFluentIterable.from;

public class InterfaceProviderManager extends ServiceManager.Listener {

    private CentralControlUnit centralControlUnit;
    private ConfigurationRepository configurationRepository;
    private Set<InterfaceProvider> interfaceProviders;
    private ServiceManager serviceManager;

    public InterfaceProviderManager(CentralControlUnit centralControlUnit, ConfigurationRepository configurationRepository, Set<InterfaceProvider> interfaceProviders) {
        this.centralControlUnit = centralControlUnit;
        this.configurationRepository = configurationRepository;
        this.interfaceProviders = interfaceProviders;
    }

    public void startInterfaces() {
        CentralControlUnitConfiguration centralControlUnitConfiguration = configurationRepository.getCentralControlUnitConfiguration(centralControlUnit);

        from(interfaceProviders)
                .forEachItem(interfaceProvider -> interfaceProvider.configure(centralControlUnitConfiguration.getConfiguration(interfaceProvider)));

        serviceManager = new ServiceManager(interfaceProviders);
        serviceManager.addListener(this);
        serviceManager.startAsync();
    }

    public void stopInterfaces() {
        serviceManager.startAsync();
    }


    @Override
    public void stopped() {
        super.stopped();
    }

    @Override
    public void failure(Service service) {
        super.failure(service);
    }
}
