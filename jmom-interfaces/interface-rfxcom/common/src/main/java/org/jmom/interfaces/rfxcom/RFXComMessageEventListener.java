package org.jmom.interfaces.rfxcom;

import org.jmom.interfaces.rfxcom.messages.RFXComBaseMessage;

import java.util.EventListener;

public interface RFXComMessageEventListener extends EventListener {

    void messageReceived(RFXComBaseMessage<?> message);

}
