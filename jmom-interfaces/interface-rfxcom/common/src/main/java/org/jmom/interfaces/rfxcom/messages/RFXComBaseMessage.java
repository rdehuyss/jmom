package org.jmom.interfaces.rfxcom.messages;

import org.jmom.core.model.things.devices.typelibrary.AbstractChange;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;

import java.util.Set;

public abstract class RFXComBaseMessage<T extends RFXComIdentifier> {

    private T identifier;

    protected RFXComBaseMessage(T identifier) {
        this.identifier = identifier;
    }

    public T getIdentifier() {
        return identifier;
    }

}