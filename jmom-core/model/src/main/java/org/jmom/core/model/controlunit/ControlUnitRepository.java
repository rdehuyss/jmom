package org.jmom.core.model.controlunit;

import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.DomainEvent;
import org.jmom.core.infrastucture.cqrs.LocalAggregateRoot;
import org.jmom.core.model.eda.commands.CreateControlUnitCommand;

public class ControlUnitRepository extends AggregateRoot implements LocalAggregateRoot {

    private ControlUnit controlUnit;

    public ControlUnit getControlUnit() {
        return controlUnit;
    }

    public boolean isControlUnitConfigured() {
        return controlUnit != null;
    }

    private void handle(ControlUnitCreatedDomainEvent domainEvent) {
        this.controlUnit = domainEvent.controlUnit;
    }

    public static class ControlUnitCreatedDomainEvent implements DomainEvent<ControlUnitRepository> {

        private final ControlUnit controlUnit;

        public ControlUnitCreatedDomainEvent(CreateControlUnitCommand command) {
            this.controlUnit = command.getControlUnit();
        }


        @Override
        public void process(ControlUnitRepository repository) {
            repository.handle(this);
        }
    }


}
