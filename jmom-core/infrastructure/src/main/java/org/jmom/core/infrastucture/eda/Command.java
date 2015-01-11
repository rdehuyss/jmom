package org.jmom.core.infrastucture.eda;

import java.util.UUID;

public abstract class Command extends Message {

    protected Command() {
        super(UUID.randomUUID());
    }

}
