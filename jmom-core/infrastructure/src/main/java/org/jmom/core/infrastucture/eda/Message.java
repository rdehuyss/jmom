package org.jmom.core.infrastucture.eda;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.joda.time.Instant;

import java.util.UUID;

@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Message {

    private UUID correlationId;
    private Instant instant;
    private boolean transmittedOverNetwork;

    private Message() {}

    protected Message(UUID correlationId) {
        this.correlationId = correlationId;
        this.instant = Instant.now();
    }

    public UUID getCorrelationId() {
        return correlationId;
    }

    public boolean hasCorrelationId() {
        return correlationId != null;
    }

    public Instant getInstant() {
        return instant;
    }

    public boolean isTransmittedOverNetwork() {
        return transmittedOverNetwork;
    }

    public void setTransmittedOverNetwork() {
        this.transmittedOverNetwork = true;
    }
}
