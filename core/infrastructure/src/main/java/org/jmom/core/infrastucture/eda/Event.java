package org.jmom.core.infrastucture.eda;

import org.joda.time.Instant;

import java.util.UUID;

public abstract class Event extends Change {

    public Event() {
        super(null);
    }

    public Event(Command command) {
        super(command.getCorrelationId());
    }

}
