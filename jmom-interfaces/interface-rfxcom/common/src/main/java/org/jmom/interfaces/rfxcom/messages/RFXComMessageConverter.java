package org.jmom.interfaces.rfxcom.messages;

public interface RFXComMessageConverter<T extends  RFXComBaseMessage> {

    /**
     * Procedure for encode raw data.
     *
     * @param data
     *            Raw data.
     */
    T decodeMessage(byte[] data);

    /**
     * Procedure for decode object to raw data.
     *
     * @return raw data.
     */
    byte[] encodeMessage(T message);


}
