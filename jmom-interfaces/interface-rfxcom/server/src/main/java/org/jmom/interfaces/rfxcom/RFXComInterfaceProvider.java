package org.jmom.interfaces.rfxcom;

import gnu.io.NoSuchPortException;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.model.interfacing.Configuration;
import org.jmom.interfaces.rfxcom.connector.RFXComConnector;
import org.jmom.interfaces.rfxcom.connector.RFXComSerialConnector;

import static org.jmom.interfaces.rfxcom.NativeLibraryTools.loadRXTX;

public class RFXComInterfaceProvider extends AbstractRFXComInterfaceProvider {

    private Configuration configuration;

    public RFXComInterfaceProvider(JMomBus jMomBus) {
        super(jMomBus);
    }

    @Override
    public void configure(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    protected void doStart() {
        try {
            loadRXTX();
            initConnection(getConnector());
            notifyStarted();
        } catch (Exception e) {
            notifyFailed(e);
        }
    }

    private RFXComConnector getConnector() throws NoSuchPortException {
        if (configuration.contains(RFXComSerialConnector.class.getSimpleName())) {
            return new RFXComSerialConnector(configuration.get(RFXComSerialConnector.class.getSimpleName()).toString());
        }
        return null;
    }
}
