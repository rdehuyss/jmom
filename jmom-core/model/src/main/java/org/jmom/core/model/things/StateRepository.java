package org.jmom.core.model.things;


import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.DomainEvent;

import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class StateRepository extends AggregateRoot {

    private Map<String, StateChangedEvent> stateChanges;

    public StateRepository() {
        stateChanges = newHashMap();
    }

    private void handle(UpdateStateChangeDomainEvent event) {
        stateChanges.put(event.deviceIdentifier.asString(), event.stateChangedEvent);
    }

    public static class UpdateStateChangeDomainEvent implements DomainEvent<StateRepository> {

        private final DeviceIdentifier deviceIdentifier;
        private final StateChangedEvent stateChangedEvent;

        public UpdateStateChangeDomainEvent(StateChangedEvent stateChangedEvent) {
            this.deviceIdentifier = stateChangedEvent.getDeviceIdentifier();
            this.stateChangedEvent = stateChangedEvent;
        }


        @Override
        public void process(StateRepository repository) {
            repository.handle(this);
        }
    }

}
