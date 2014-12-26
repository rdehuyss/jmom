package org.jmom.interfaces.rfxcom.connector;

import java.util.EventObject;

public class RFXComMessageReceivedEvent extends EventObject {


    public RFXComMessageReceivedEvent(Object source) {
        super(source);
    }

    public void MessageReceivedEvent(byte[] packet) {
    }

}