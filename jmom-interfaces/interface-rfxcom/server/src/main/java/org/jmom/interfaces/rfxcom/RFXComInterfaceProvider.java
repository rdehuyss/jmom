package org.jmom.interfaces.rfxcom;

import com.google.common.util.concurrent.AbstractService;
import org.jmom.core.infrastucture.bus.JMomEventBus;
import org.jmom.core.model.configuration.Configuration;
import org.jmom.core.model.eda.StateChangedByInterfaceEvent;
import org.jmom.core.model.interfacing.InterfaceProvider;
import org.jmom.interfaces.rfxcom.connector.RFXComSerialConnector;
import org.jmom.interfaces.rfxcom.messages.RFXComBaseMessage;
import org.jmom.interfaces.rfxcom.messages.RFXComBaseStateChangeMessage;

import static com.google.common.base.Preconditions.checkState;

public class RFXComInterfaceProvider extends AbstractRFXComInterfaceProvider {

    private RFXComConnection rfxComConnection;

    public RFXComInterfaceProvider(JMomEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void configure(Configuration configuration) {

    }

    @Override
    protected void doStart() {
        try {
            boolean hasRxTxLibrary = NativeLibraryTools.hasNativeRFXComLibrary();
            if (!hasRxTxLibrary) {
                hasRxTxLibrary = NativeLibraryTools.loadEmbeddedLibrary();
            }
            checkState(hasRxTxLibrary, "No RxTx library is available.");


            RFXComSerialConnector rfxComSerialConnector = new RFXComSerialConnector("/dev/ttyUSB0");
            rfxComConnection = new RFXComConnection(rfxComSerialConnector);
            rfxComConnection.setRfxComMessageEventListener(this);
            rfxComConnection.connect();

            notifyStarted();
        } catch (Exception e) {
            notifyFailed(e);
        }
    }

    @Override
    protected void doStop() {
        rfxComConnection.disconnect();
        notifyStopped();
    }
}
