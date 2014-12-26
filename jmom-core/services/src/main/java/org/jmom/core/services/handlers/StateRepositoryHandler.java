package org.jmom.core.services.handlers;

import com.google.common.eventbus.Subscribe;
import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.StateRepository.UpdateStateChangeDomainEvent;

import java.io.IOException;

public class StateRepositoryHandler implements Handler {

    private StateRepository stateRepository;
    private Repo repo;

    public StateRepositoryHandler(StateRepository stateRepository, Repo repo) {
        this.stateRepository = stateRepository;
        this.repo = repo;
    }

    @Subscribe
    public void handle(StateChangedEvent stateChangedEvent) throws IOException {
        stateRepository.apply(new UpdateStateChangeDomainEvent(stateChangedEvent));
        repo.save(stateRepository);
    }
}
