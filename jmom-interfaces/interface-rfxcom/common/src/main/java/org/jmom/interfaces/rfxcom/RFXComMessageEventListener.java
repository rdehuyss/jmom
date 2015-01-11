package org.jmom.interfaces.rfxcom;

import org.jmom.core.model.eda.events.StateChangedEvent;

import java.util.EventListener;

public interface RFXComMessageEventListener extends EventListener {

    void onStateChangedEvent(StateChangedEvent message);

}
