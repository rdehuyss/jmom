package org.jmom.interfaces.rfxcom;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractService;
import org.jmom.core.infrastucture.bus.JMomEventBus;
import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.core.model.eda.StateChangedByInterfaceEvent;
import org.jmom.core.model.interfacing.InterfaceProvider;
import org.jmom.core.model.things.devices.typelibrary.AbstractChange;
import org.jmom.core.model.things.devices.typelibrary.StateChange;
import org.jmom.interfaces.rfxcom.messages.RFXComBaseMessage;
import org.jmom.interfaces.rfxcom.messages.RFXComBaseStateChangeMessage;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;

import java.util.Set;

public abstract class AbstractRFXComInterfaceProvider extends AbstractService implements InterfaceProvider, RFXComMessageEventListener {

    public static final String NAME = "RFXCom";
    private RFXComConnection rfxComConnection;
    private JMomEventBus eventBus;

    public AbstractRFXComInterfaceProvider(JMomEventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public void messageReceived(RFXComBaseMessage<?> message) {
        if (message instanceof RFXComBaseStateChangeMessage) {
            RFXComBaseStateChangeMessage stateChangeMessage = (RFXComBaseStateChangeMessage) message;
            StateChangedByInterfaceEvent stateChangedEvent = new StateChangedByInterfaceEvent(message.getIdentifier(), stateChangeMessage.getStateChange());

            eventBus.post(stateChangedEvent);
        }
    }

}
