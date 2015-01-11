package org.jmom.core.services.handlers;

import com.google.common.eventbus.Subscribe;
import org.jmom.core.model.eda.commands.DeleteThingCommand;
import org.jmom.core.model.eda.commands.SaveThingCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.eda.commands.UpdateThingCommand;
import org.jmom.core.model.things.ThingRepository;
import org.jmom.core.model.things.ThingRepository.DeleteThingDomainEvent;
import org.jmom.core.model.things.ThingRepository.SaveThingDomainEvent;
import org.jmom.core.model.things.ThingRepository.UpdateStateChangeDomainEvent;
import org.jmom.core.model.things.ThingRepository.UpdateThingDomainEvent;

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
        thingRepository.apply(new SaveThingDomainEvent(saveThingCommand));
        repo.save(thingRepository);
    }

    @Subscribe
    public void handle(UpdateThingCommand updateThingCommand) throws IOException {
        thingRepository.apply(new UpdateThingDomainEvent(updateThingCommand));
        repo.save(thingRepository);
    }

    @Subscribe
    public void handle(DeleteThingCommand deleteThingCommand) throws IOException {
        thingRepository.apply(new DeleteThingDomainEvent(deleteThingCommand));
        repo.save(thingRepository);
    }

    @Subscribe
    public void handle(StateChangedEvent stateChangedEvent) {
        thingRepository.applyInMemoryOnly(new UpdateStateChangeDomainEvent(stateChangedEvent));
    }
}
