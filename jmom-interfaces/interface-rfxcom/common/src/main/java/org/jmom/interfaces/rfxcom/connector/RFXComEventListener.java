package org.jmom.interfaces.rfxcom.connector;

import java.util.EventListener;
import java.util.EventObject;

/**
 * This interface defines interface to receive data from RFXCOM controller.
 *
 * @author Pauli Anttila
 * @since 1.2.0
 */
public interface RFXComEventListener extends EventListener {

    /**
     * Procedure for receive raw data from RFXCOM controller.
     *
     * @param data Received raw data.
     */
    void packetReceived(RFXComMessageReceivedEvent event, byte[] data);

}
