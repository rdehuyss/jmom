package org.jmom.core.services.handlers;

import com.google.common.eventbus.Subscribe;
import org.jmom.core.model.eda.SaveThingCommand;
import org.jmom.core.model.eda.StateChangedByInterfaceEvent;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.things.ThingRepository;
import org.jmom.core.model.things.ThingRepository.UpdateOrSaveThingDomainEvent;
import org.jmom.core.model.things.ThingRepository.UpdateStateChangeDomainEvent;

import java.io.IOException;

public class ThingRepositoryHandler implements Handler {

    private ThingRepository thingRepository;
    private Repo repo;

    public ThingRepositoryHandler(ThingRepository thingRepository, Repo repo) {
        this.thingRepository = thingRepository;
        this.repo = repo;
    }

    @Subscribe
    public void handle(SaveThingCommand saveThingCommand) throws IOException {
        thingRepository.apply(new UpdateOrSaveThingDomainEvent(saveThingCommand));
        repo.save(thingRepository);
    }

    @Subscribe
    public void handle(StateChangedByInterfaceEvent stateChangedEvent) {
        thingRepository.applyInMemoryOnly(new UpdateStateChangeDomainEvent(stateChangedEvent));
    }
}
