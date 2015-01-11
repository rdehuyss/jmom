package org.jmom.core.services.handlers;

import com.google.common.eventbus.Subscribe;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.configuration.ConfigurationRepository.InterfaceDiscoveryFinishedDomainEvent;
import org.jmom.core.model.configuration.ConfigurationRepository.LinkCentralControlUnitToResidenceDomainEvent;
import org.jmom.core.model.configuration.ConfigurationRepository.SaveInterfaceProviderConfigurationDomainEvent;
import org.jmom.core.model.configuration.ConfigurationRepository.StartInterfaceDiscoveryDomainEvent;
import org.jmom.core.model.eda.commands.LinkCentralControlUnitToResidenceCommand;
import org.jmom.core.model.eda.events.InterfaceDiscoveryStartedEvent;
import org.jmom.core.model.eda.events.InterfaceDiscoveryFinishedEvent;
import org.jmom.core.model.eda.events.InterfaceProviderFoundEvent;

import java.io.IOException;

public class ConfigurationRepositoryHandler implements Handler {

    private ConfigurationRepository configurationRepository;
    private Repo repo;

    public ConfigurationRepositoryHandler(ConfigurationRepository configurationRepository, Repo repo) {
        this.configurationRepository = configurationRepository;
        this.repo = repo;
    }

    @Subscribe
    public void handle(InterfaceDiscoveryStartedEvent event) throws IOException {
        configurationRepository.apply(new StartInterfaceDiscoveryDomainEvent(event));
        repo.save(configurationRepository);
    }

    @Subscribe
    public void handle(InterfaceDiscoveryFinishedEvent event) throws IOException {
        configurationRepository.apply(new InterfaceDiscoveryFinishedDomainEvent(event));
        repo.save(configurationRepository);
    }

    @Subscribe
    public void handle(InterfaceProviderFoundEvent event) throws IOException {
        configurationRepository.apply(new SaveInterfaceProviderConfigurationDomainEvent(event));
        repo.save(configurationRepository);
    }

    @Subscribe
    public void handle(LinkCentralControlUnitToResidenceCommand command) throws IOException {
        configurationRepository.apply(new LinkCentralControlUnitToResidenceDomainEvent(command));
        repo.save(configurationRepository);
    }
}
