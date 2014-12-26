package org.jmom.interfaces.rfxcom;

import org.jmom.core.infrastucture.bus.JMomEventBus;
import org.jmom.core.model.configuration.Configuration;
import org.jmom.interfaces.rfxcom.connector.RFXComSerialConnector;

import static com.google.common.base.Preconditions.checkState;

public class RFXComInterfaceProvider extends AbstractRFXComInterfaceProvider {

    public RFXComInterfaceProvider(JMomEventBus eventBus) {
        super(eventBus);
    }

    @Override
    public void configure(Configuration configuration) {

    }

    @Override
    protected void doStart() {
        try {
            loadRXTX();
            initConnection(new RFXComSerialConnector("/dev/ttyUSB0"));
            notifyStarted();
        } catch (Exception e) {
            notifyFailed(e);
        }
    }

    private void loadRXTX() {
        boolean hasRxTxLibrary = NativeLibraryTools.hasNativeRFXComLibrary();
        if (!hasRxTxLibrary) {
            hasRxTxLibrary = NativeLibraryTools.loadEmbeddedLibrary();
        }
        checkState(hasRxTxLibrary, "No RxTx library is available.");
    }
}
