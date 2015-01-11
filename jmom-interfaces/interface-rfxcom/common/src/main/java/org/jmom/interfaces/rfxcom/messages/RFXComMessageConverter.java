package org.jmom.interfaces.rfxcom.messages;

import org.jmom.core.model.eda.commands.ChangeStateCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;

public interface RFXComMessageConverter {

    /**
     * Procedure for encode raw data.
     *
     * @param data
     *            Raw data.
     */
    StateChangedEvent decodeMessage(byte[] data);

    /**
     * Procedure for decode object to raw data.
     *
     * @return raw data.
     */
    byte[] encodeMessage(ChangeStateCommand message);


}
