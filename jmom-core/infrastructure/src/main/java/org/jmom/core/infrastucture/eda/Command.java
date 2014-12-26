package org.jmom.core.infrastucture.eda;

import org.joda.time.Instant;

import java.util.UUID;

public abstract class Command extends Change {

    protected Command() {
        super(UUID.randomUUID());
    }

}
