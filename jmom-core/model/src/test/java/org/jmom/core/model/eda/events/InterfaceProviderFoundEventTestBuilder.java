package org.jmom.core.model.eda.events;

import com.google.common.collect.Maps;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;

import java.util.Map;
import java.util.UUID;

public class InterfaceProviderFoundEventTestBuilder {

    private InterfaceDiscoverer interfaceDiscoverer;
    private Map<String, Object> configuration;
    private CentralControlUnit centralControlUnit;

    private InterfaceProviderFoundEventTestBuilder() {

    }

    public static InterfaceProviderFoundEventTestBuilder anInterfaceProviderFoundEvent() {
        return new InterfaceProviderFoundEventTestBuilder();
    }

    public InterfaceProviderFoundEvent build() {
        InterfaceProviderFoundEvent interfaceProviderFoundEvent = new InterfaceProviderFoundEvent(interfaceDiscoverer, configuration);
        interfaceProviderFoundEvent.setCentralControlUnit(centralControlUnit);
        return interfaceProviderFoundEvent;
    }

    public InterfaceProviderFoundEventTestBuilder withInterfaceDiscoverer(InterfaceDiscoverer interfaceDiscoverer) {
        this.interfaceDiscoverer = interfaceDiscoverer;
        return this;
    }

    public InterfaceProviderFoundEventTestBuilder withConfiguration(Map<String, Object> configuration) {
        this.configuration = configuration;
        return this;
    }

    public InterfaceProviderFoundEventTestBuilder withConfiguration(String key, Object value) {
        this.configuration = Maps.newHashMap();
        this.configuration.put(key, value);
        return this;
    }

    public InterfaceProviderFoundEventTestBuilder withCentralControlUnit(CentralControlUnit centralControlUnit) {
        this.centralControlUnit = centralControlUnit;
        return this;
    }
}