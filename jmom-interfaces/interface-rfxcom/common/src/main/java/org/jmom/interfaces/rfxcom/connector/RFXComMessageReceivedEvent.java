package org.jmom.interfaces.rfxcom.connector;

import java.util.EventObject;

public class RFXComMessageReceivedEvent extends EventObject {


    private byte[] msg;

    public RFXComMessageReceivedEvent(Object source, byte[] msg) {
        super(source);
        this.msg = msg;
    }

    public byte[] getMessage() {
        return msg;
    }
}