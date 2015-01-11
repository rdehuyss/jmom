package org.jmom.interfaces.rfxcom;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.AbstractService;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.model.eda.commands.ChangeStateCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.interfacing.InterfaceProvider;
import org.jmom.interfaces.rfxcom.connector.RFXComConnector;
import rx.Observable;

public abstract class AbstractRFXComInterfaceProvider extends AbstractService implements InterfaceProvider, RFXComMessageEventListener, JMomBusAware {

    public static final String NAME = "RFXCom";
    private RFXComConnection rfxComConnection;
    private JMomBus jMomBus;
    private Observable<StateChangedEvent> stateChanges;

    public AbstractRFXComInterfaceProvider(JMomBus jMomBus) {
        this.jMomBus = jMomBus;
    }

    @Override
    public String name() {
        return NAME;
    }

    protected void initConnection(RFXComConnector connector) throws Exception {
        rfxComConnection = new RFXComConnection(connector);
        rfxComConnection.setRfxComMessageEventListener(this);
        rfxComConnection.connect();
        stateChanges = rfxComConnection.stateChanges();
        stateChanges.subscribe(stateChangedEvent -> System.out.println("Event received in RXJava " + stateChangedEvent.toString()));
    }

    @Override
    protected void doStop() {
        rfxComConnection.disconnect();
        notifyStopped();
    }

    @Override
    public void onStateChangedEvent(StateChangedEvent message) {
        if(message.getNewState() != null) {
            jMomBus.post(message);
        }
    }

    @Subscribe
    public void onChangeStateCommand(ChangeStateCommand command) {
        rfxComConnection.sendCommand(command);
    }

}
