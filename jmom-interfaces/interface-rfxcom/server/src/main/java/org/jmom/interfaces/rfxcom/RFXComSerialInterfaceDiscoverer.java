package org.jmom.interfaces.rfxcom;

import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AbstractExecutionThreadService;
import gnu.io.CommPortIdentifier;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.model.eda.events.InterfaceProviderFoundEvent;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.interfacing.HardwareDependency;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;
import org.jmom.interfaces.rfxcom.connector.RFXComSerialConnector;
import org.jmom.interfaces.rfxcom.messages.RFXComInterfaceMessageConverter.RFXComInterfaceMessage;

import java.util.Enumeration;

import static org.jmom.core.model.interfacing.HardwareDependency.RXTX_COMM_PORT;

public class RFXComSerialInterfaceDiscoverer extends AbstractExecutionThreadService implements InterfaceDiscoverer, RFXComMessageEventListener {

    private final JMomBus jMomBus;
    private boolean rfxcomNotFound = true;
    private CommPortIdentifier currentPortIdentifier;

    public RFXComSerialInterfaceDiscoverer(JMomBus jMomBus) {
        this.jMomBus = jMomBus;
    }

    @Override
    public String name() {
        return RFXComInterfaceProvider.NAME;
    }

    @Override
    protected void run() throws Exception {
        HardwareDependency.waitAndTakeLock(RXTX_COMM_PORT);
        NativeLibraryTools.loadRXTX();

        Enumeration portIdentifiers = CommPortIdentifier.getPortIdentifiers();
        RFXComConnection rfxComConnection = null;
        while (portIdentifiers.hasMoreElements() && rfxcomNotFound) {
            currentPortIdentifier = (CommPortIdentifier) portIdentifiers.nextElement();
            if (currentPortIdentifier.getPortType() == CommPortIdentifier.PORT_SERIAL) {
                try {
                    RFXComSerialConnector rfxComSerialConnector = new RFXComSerialConnector(currentPortIdentifier);

                    rfxComConnection = new RFXComConnection(rfxComSerialConnector);
                    rfxComConnection.setRfxComMessageEventListener(this);
                    rfxComConnection.connect();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    //no rfxcom apparently
                } finally {
                    disconnect(rfxComConnection);
                }
            }
        }

        HardwareDependency.finishLock(RXTX_COMM_PORT);
        super.triggerShutdown();
    }


    @Override
    public void onStateChangedEvent(StateChangedEvent message) {
        if (message instanceof RFXComInterfaceMessage) {
            rfxcomNotFound = false;
            jMomBus.post(new InterfaceProviderFoundEvent(this, RFXComSerialConnector.class.getSimpleName(), currentPortIdentifier.getName()));
        }
    }

    private void disconnect(RFXComConnection rfxComConnection) {
        if (rfxComConnection != null) {
            rfxComConnection.disconnect();
        }
    }

    public static void main(String[] args) throws Exception {
        RFXComSerialInterfaceDiscoverer rfxComSerialInterfaceDiscoverer = new RFXComSerialInterfaceDiscoverer(new JMomBus());
        rfxComSerialInterfaceDiscoverer.run();
    }


}
