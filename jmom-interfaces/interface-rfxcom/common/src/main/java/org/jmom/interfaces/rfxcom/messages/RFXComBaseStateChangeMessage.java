package org.jmom.interfaces.rfxcom.messages;

import org.jmom.core.model.things.devices.typelibrary.StateChange;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;

public abstract class RFXComBaseStateChangeMessage<T extends RFXComIdentifier> extends RFXComBaseMessage<T> {

    protected RFXComBaseStateChangeMessage(T identifier) {
        super(identifier);
    }

    public abstract StateChange getStateChange();
}
