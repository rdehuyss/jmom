package org.jmom.core.model.configuration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.DomainEvent;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.eda.commands.LinkCentralControlUnitToResidenceCommand;
import org.jmom.core.model.eda.events.InterfaceDiscoveryFinishedEvent;
import org.jmom.core.model.eda.events.InterfaceDiscoveryStartedEvent;
import org.jmom.core.model.eda.events.InterfaceProviderFoundEvent;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.Residence;
import org.joda.time.Instant;

import java.util.Map;
import java.util.Set;

import static com.google.common.collect.JMomFluentIterable.from;
import static com.google.common.collect.Sets.newHashSet;

public class ConfigurationRepository extends AggregateRoot {

    private Map<Path, ResidenceConfiguration> configuration;
    private Map<CentralControlUnit, CentralControlUnitConfiguration> unknownCentralControlUnitInterfaceProviderConfiguration;

    public ConfigurationRepository() {
        this(Maps.newHashMap(), Maps.newHashMap());
    }

    @JsonCreator
    private ConfigurationRepository(
            @JsonProperty("configuration") Map<Path, ResidenceConfiguration> configuration,
            @JsonProperty("unknownCentralControlUnitInterfaceProviderConfiguration") Map<CentralControlUnit, CentralControlUnitConfiguration> unknownCentralControlUnitInterfaceProviderConfiguration) {
        this.configuration = configuration;
        this.unknownCentralControlUnitInterfaceProviderConfiguration = unknownCentralControlUnitInterfaceProviderConfiguration;
    }

    public ResidenceConfiguration getOrCreateResidenceConfiguration(Residence residence) {
        if (!configuration.containsKey(residence.getPath())) {
            configuration.put(residence.getPath(), new ResidenceConfiguration(residence));
        }
        return configuration.get(residence.getPath());
    }

    public Set<ResidenceConfiguration> getResidenceConfigurations() {
        return newHashSet(configuration.values());
    }

    public boolean isCentralControlUnitConfigured(CentralControlUnit centralControlUnit) {
        return getCentralControlUnitConfigurationFromResidenceConfigurationAsOptional(centralControlUnit).isPresent();
    }

    public CentralControlUnitConfiguration getCentralControlUnitConfiguration(CentralControlUnit centralControlUnit) {
        Optional<CentralControlUnitConfiguration> centralControlUnitConfigurationOptional = getCentralControlUnitConfigurationFromResidenceConfigurationAsOptional(centralControlUnit);
        if (centralControlUnitConfigurationOptional.isPresent()) {
            return centralControlUnitConfigurationOptional.get();
        } else {
            if (!unknownCentralControlUnitInterfaceProviderConfiguration.containsKey(centralControlUnit)) {
                unknownCentralControlUnitInterfaceProviderConfiguration.put(centralControlUnit, new CentralControlUnitConfiguration());
            }
            return unknownCentralControlUnitInterfaceProviderConfiguration.get(centralControlUnit);
        }
    }

    public boolean hasUnknownCentralControlUnitConfiguration() {
        return !unknownCentralControlUnitInterfaceProviderConfiguration.isEmpty();
    }

    public Set<CentralControlUnit> getUnknownCentralControlUnitConfigurations() {
        return unknownCentralControlUnitInterfaceProviderConfiguration.keySet();
    }

    private Optional<CentralControlUnitConfiguration> getCentralControlUnitConfigurationFromResidenceConfigurationAsOptional(CentralControlUnit centralControlUnit) {
        return from(getResidenceConfigurations())
                .firstMatch(residenceConfig -> residenceConfig.containsConfiguration(centralControlUnit))
                .transform(residenceConfig -> residenceConfig.getConfiguration(centralControlUnit));
    }

    private void handle(SaveInterfaceProviderConfigurationDomainEvent domainEvent) {
        CentralControlUnitConfiguration centralControlUnitConfiguration = getCentralControlUnitConfiguration(domainEvent.centralControlUnit);
        InterfaceProviderConfiguration interfaceProviderConfiguration = centralControlUnitConfiguration.getOrCreateConfiguration(domainEvent.interfaceProviderName);
        interfaceProviderConfiguration.putAll(domainEvent.configuration);
    }


    private void handle(LinkCentralControlUnitToResidenceDomainEvent domainEvent) {
        ResidenceConfiguration residenceConfiguration = getOrCreateResidenceConfiguration(domainEvent.residence);
        residenceConfiguration.saveCentralControlUnitConfiguration(domainEvent.centralControlUnit, unknownCentralControlUnitInterfaceProviderConfiguration.remove(domainEvent.centralControlUnit));
    }

    private void handle(StartInterfaceDiscoveryDomainEvent domainEvent) {
        getCentralControlUnitConfiguration(domainEvent.centralControlUnit).startDiscovery(domainEvent);
    }

    private void handle(InterfaceDiscoveryFinishedDomainEvent domainEvent) {
        getCentralControlUnitConfiguration(domainEvent.centralControlUnit).stopDiscovery(domainEvent);
    }


    public static class SaveInterfaceProviderConfigurationDomainEvent implements DomainEvent<ConfigurationRepository> {

        private final CentralControlUnit centralControlUnit;
        private final String interfaceProviderName;
        private final Map<String, Object> configuration;

        public SaveInterfaceProviderConfigurationDomainEvent(InterfaceProviderFoundEvent event) {
            this.centralControlUnit = event.getCentralControlUnit();
            this.interfaceProviderName = event.getInterfaceProviderName();
            this.configuration = event.getConfiguration();
        }

        @Override
        public void process(ConfigurationRepository configurationRepository) {
            configurationRepository.handle(this);
        }
    }

    public static class LinkCentralControlUnitToResidenceDomainEvent implements DomainEvent<ConfigurationRepository> {

        private final Residence residence;
        private final CentralControlUnit centralControlUnit;

        public LinkCentralControlUnitToResidenceDomainEvent(LinkCentralControlUnitToResidenceCommand command) {
            residence = command.getResidence();
            centralControlUnit = command.getCentralControlUnit();
        }

        @Override
        public void process(ConfigurationRepository configurationRepository) {
            configurationRepository.handle(this);
        }
    }

    public static class StartInterfaceDiscoveryDomainEvent implements DomainEvent<ConfigurationRepository> {

        private final CentralControlUnit centralControlUnit;
        private final Instant instant;

        public StartInterfaceDiscoveryDomainEvent(InterfaceDiscoveryStartedEvent event) {
            centralControlUnit = event.getCentralControlUnit();
            instant = event.getInstant();
        }

        @Override
        public void process(ConfigurationRepository configurationRepository) {
            configurationRepository.handle(this);
        }

        public Instant getInstant() {
            return instant;
        }
    }

    public static class InterfaceDiscoveryFinishedDomainEvent implements DomainEvent<ConfigurationRepository> {

        private final CentralControlUnit centralControlUnit;
        private final Instant instant;

        public InterfaceDiscoveryFinishedDomainEvent(InterfaceDiscoveryFinishedEvent event) {
            centralControlUnit = event.getCentralControlUnit();
            instant = event.getInstant();
        }

        @Override
        public void process(ConfigurationRepository configurationRepository) {
            configurationRepository.handle(this);
        }

        public Instant getInstant() {
            return instant;
        }
    }


}
