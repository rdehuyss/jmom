package org.jmom.core.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jmom.core.model.configuration.ConfigurationRepository.InterfaceDiscoveryFinishedDomainEvent;
import org.jmom.core.model.configuration.ConfigurationRepository.StartInterfaceDiscoveryDomainEvent;
import org.jmom.core.model.interfacing.InterfaceProvider;
import org.joda.time.Instant;

import java.util.LinkedList;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class CentralControlUnitConfiguration {

    private final Map<String, InterfaceProviderConfiguration> configuration;
    private final LinkedList<InterfaceDiscoveryEvent> interfaceDiscoveryEvents;

    CentralControlUnitConfiguration() {
        this(Maps.newHashMap(), Lists.newLinkedList());
    }

    @JsonCreator
    private CentralControlUnitConfiguration(
            @JsonProperty("configuration") Map<String, InterfaceProviderConfiguration> configuration,
            @JsonProperty("interfaceDiscoveryEvents") LinkedList<InterfaceDiscoveryEvent> interfaceDiscoveryEvents) {
        this.configuration = configuration;
        this.interfaceDiscoveryEvents = interfaceDiscoveryEvents;
    }

    void save(InterfaceProviderConfiguration  interfaceProviderConfiguration) {
        configuration.put(interfaceProviderConfiguration.getInterfaceProviderName(), interfaceProviderConfiguration);
    }

    public boolean containsConfiguration(InterfaceProvider interfaceProvider) {
        return configuration.containsKey(interfaceProvider.name());
    }

    public InterfaceProviderConfiguration getConfiguration(InterfaceProvider interfaceProvider) {
        return getOrCreateConfiguration(interfaceProvider.name());
    }

    public InterfaceProviderConfiguration getOrCreateConfiguration(String interfaceProviderName) {
        if(!configuration.containsKey(interfaceProviderName)) {
            configuration.put(interfaceProviderName, new InterfaceProviderConfiguration(interfaceProviderName));
        }
        return configuration.get(interfaceProviderName);
    }

    public boolean isDiscoveryInProgress() {
        return interfaceDiscoveryEvents.getLast().isDiscoveryInProgress();
    }

    void startDiscovery(StartInterfaceDiscoveryDomainEvent startedEvent) {
        System.out.println("Configuration started");
        interfaceDiscoveryEvents.add(new InterfaceDiscoveryEvent(startedEvent.getInstant()));
    }

    void stopDiscovery(InterfaceDiscoveryFinishedDomainEvent stopEvent) {
        System.out.println("Configuration finished");
        interfaceDiscoveryEvents.getLast().end(stopEvent.getInstant());
    }

    public static class InterfaceDiscoveryEvent {
        private Instant start;
        private Instant end;

        private InterfaceDiscoveryEvent() {}

        private InterfaceDiscoveryEvent(Instant start) {
            this.start = start;
        }

        private void end(Instant end) {
            this.end = end;
        }

        public Instant getStart() {
            return start;
        }

        public Instant getEnd() {
            return end;
        }

        public boolean isDiscoveryInProgress() {
            return end == null;
        }
    }
}
