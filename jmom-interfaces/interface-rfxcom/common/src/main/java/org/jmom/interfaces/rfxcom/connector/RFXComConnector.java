package org.jmom.interfaces.rfxcom.connector;

import java.io.IOException;

/**
 * This interface defines interface to communicate RFXCOM controller.
 *
 * @author Pauli Anttila, Evert van Es
 * @since 1.2.0
 */
public interface RFXComConnector {

    /**
     * Procedure for connecting to RFXCOM controller.
     */
    public void connect() throws Exception;


    /**
     * Procedure for disconnecting to RFXCOM controller.
     *
     */
    public void disconnect();


    /**
     * Procedure for send raw data to RFXCOM controller.
     *
     * @param data
     *            raw bytes.
     */
    public void sendMessage(byte[] data) throws IOException;

    /**
     * Procedure for register event listener.
     *
     * @param listener
     *            Event listener instance to handle eventhandlers.
     */
    public void addEventListener(RFXComEventListener listener);

    /**
     * Procedure for remove event listener.
     *
     * @param listener
     *            Event listener instance to remove.
     */
    public void removeEventListener(RFXComEventListener listener);

}