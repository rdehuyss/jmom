package org.jmom.interfaces.rfxcom;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractService;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.infrastucture.bus.JMomEventBus;
import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.core.model.interfacing.InterfaceProvider;
import org.jmom.interfaces.rfxcom.connector.RFXComConnector;
import org.jmom.interfaces.rfxcom.messages.RFXComBaseMessage;
import org.jmom.interfaces.rfxcom.messages.RFXComBaseStateChangeMessage;
import org.jmom.interfaces.rfxcom.messages.RFXComLighting1MessageConverter;

public abstract class AbstractRFXComInterfaceProvider extends AbstractService implements InterfaceProvider, RFXComMessageEventListener, JMomBusAware {

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

    protected void initConnection(RFXComConnector connector) throws Exception {
        rfxComConnection = new RFXComConnection(connector);
        rfxComConnection.setRfxComMessageEventListener(this);
        rfxComConnection.connect();
    }

    @Override
    protected void doStop() {
        rfxComConnection.disconnect();
        notifyStopped();
    }

    @Override
    public void onStateChangedEvent(StateChangedEvent message) {
        if(message.getNewState() != null) {
            eventBus.post(message);
        }
    }

    @Subscribe
    public void onChangeStateCommand(ChangeStateCommand command) {
        rfxComConnection.sendCommand(command);
    }

}
