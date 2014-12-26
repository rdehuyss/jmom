package org.jmom.interfaces.rfxcom;

import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.interfaces.rfxcom.connector.RFXComConnector;
import org.jmom.interfaces.rfxcom.connector.RFXComEventListener;
import org.jmom.interfaces.rfxcom.messages.RFXComMessageConverter;
import org.jmom.interfaces.rfxcom.messages.RFXComMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.util.EventObject;

import static com.google.common.base.Throwables.propagate;

public class RFXComConnection {

    private static final Logger logger = LoggerFactory.getLogger(RFXComConnection.class);

    private byte[] setMode = null;

    private RFXComEventListener rfxComEventListener = new MessageListener();

    private RFXComMessageEventListener rfxComMessageEventListener;

    private RFXComConnector rfxComConnector;

    public RFXComConnection(RFXComConnector rfxComConnector) {
        this.rfxComConnector = rfxComConnector;
    }

    public void setRfxComMessageEventListener(RFXComMessageEventListener rfxComMessageEventListener) {
        this.rfxComMessageEventListener = rfxComMessageEventListener;
    }

    public void connect() throws Exception {
        rfxComConnector.addEventListener(rfxComEventListener);
        rfxComConnector.connect();

        logger.debug("Reset controller");
        rfxComConnector.sendMessage(RFXComMessageFactory.CMD_RESET);

        // controller does not response immediately after reset,
        // so wait a while
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw propagate(e);
        }

        if (setMode != null) {
            try {
                logger.debug("Set mode: {}", DatatypeConverter.printHexBinary(setMode));
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("setMode ", e);
            }

            rfxComConnector.sendMessage(setMode);
        } else {
            rfxComConnector.sendMessage(RFXComMessageFactory.CMD_STATUS);
        }
    }

    public void disconnect() {
        rfxComConnector.disconnect();
    }

    public void sendCommand(ChangeStateCommand command) {
        try {
            RFXComMessageConverter converter = RFXComMessageFactory.getConverter(command);
            byte[] commandAsBytes = converter.encodeMessage(command);
            rfxComConnector.sendMessage(commandAsBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class MessageListener implements RFXComEventListener {

        @Override
        public void packetReceived(EventObject event, byte[] data) {
            RFXComMessageConverter converter = RFXComMessageFactory.getConverter(data);
            if (converter != null) {
                StateChangedEvent message = converter.decodeMessage(data);
                System.out.println("Data received:\n" + message);
                if (rfxComMessageEventListener != null) {
                    rfxComMessageEventListener.onStateChangedEvent(message);
                }
            } else {
                System.out.println("Data received:\n" + DatatypeConverter.printHexBinary(data));
            }
        }

    }

}
