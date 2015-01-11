package org.jmom.core.infrastucture.eda;

public abstract class Event extends Message {

    public Event() {
        super(null);
    }

    public Event(Command command) {
        super(command.getCorrelationId());
    }

}
