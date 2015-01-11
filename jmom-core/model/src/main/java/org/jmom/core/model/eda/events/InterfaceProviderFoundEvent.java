package org.jmom.core.model.eda.events;

import com.google.common.collect.Maps;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;

import java.util.Map;

public class InterfaceProviderFoundEvent extends InterfaceProviderEvent {

    private String interfaceProviderName;
    private Map<String, Object> configuration;

    protected InterfaceProviderFoundEvent() {

    }

    public InterfaceProviderFoundEvent(InterfaceDiscoverer interfaceDiscoverer, String key, Object value) {
        this(interfaceDiscoverer, Maps.newHashMap());
        configuration.put(key, value);
    }

    public InterfaceProviderFoundEvent(InterfaceDiscoverer interfaceDiscoverer, Map<String, Object> configuration) {
        this.interfaceProviderName = interfaceDiscoverer.name();
        this.configuration = configuration;
    }

    public String getInterfaceProviderName() {
        return interfaceProviderName;
    }

    public Map<String, Object> getConfiguration() {
        return configuration;
    }
}
