package org.jmom.core.services.handlers;

import com.google.common.eventbus.Subscribe;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.controlunit.ControlUnitRepository;
import org.jmom.core.model.controlunit.ControlUnitRepository.ControlUnitCreatedDomainEvent;
import org.jmom.core.model.eda.commands.CreateControlUnitCommand;
import org.jmom.core.model.eda.events.InterfaceDiscoveryStartedEvent;

import java.io.IOException;

public class ControlUnitRepositoryHandler implements Handler {

    private ControlUnitRepository controlUnitRepository;
    private Repo repo;
    private JMomBus jMomBus;

    public ControlUnitRepositoryHandler(ControlUnitRepository controlUnitRepository, Repo repo, JMomBus jMomBus) {
        this.controlUnitRepository = controlUnitRepository;
        this.repo = repo;
        this.jMomBus = jMomBus;
        this.jMomBus.register(this);
    }

    @Subscribe
    public void handle(CreateControlUnitCommand command) throws IOException {
        controlUnitRepository.apply(new ControlUnitCreatedDomainEvent(command));
        repo.save(controlUnitRepository);
    }

}
