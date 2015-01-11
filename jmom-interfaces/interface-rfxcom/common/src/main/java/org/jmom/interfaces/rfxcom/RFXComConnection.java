package org.jmom.interfaces.rfxcom;

import com.google.common.base.Optional;
import org.jmom.core.model.eda.commands.ChangeStateCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.interfaces.rfxcom.connector.RFXComConnector;
import org.jmom.interfaces.rfxcom.connector.RFXComEventListener;
import org.jmom.interfaces.rfxcom.connector.RFXComMessageReceivedEvent;
import org.jmom.interfaces.rfxcom.messages.RFXComMessageConverter;
import org.jmom.interfaces.rfxcom.messages.RFXComMessageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

import static com.google.common.base.Throwables.propagate;

public class RFXComConnection {

    private static final Logger logger = LoggerFactory.getLogger(RFXComConnection.class);

    private byte[] setMode = null;

    private RFXComEventListener rfxComEventListener = new MessageListener();

    private RFXComMessageEventListener rfxComMessageEventListener;

    private RFXComConnector rfxComConnector;

    private Observable<StateChangedEvent> stateChanges;

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

        stateChanges = rfxComConnector.data()
                .map(this::toStateChangedEvent)
                .filter(Optional::isPresent)
                .map(Optional::get);

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
        public void packetReceived(RFXComMessageReceivedEvent event, byte[] data) {
            Optional<StateChangedEvent> message = toStateChangedEvent(event);
            if (message.isPresent()) {
                System.out.println("Data received:\n" + message.get());
                if (rfxComMessageEventListener != null) {
                    rfxComMessageEventListener.onStateChangedEvent(message.get());
                }
            } else {
                System.out.println("Data received:\n" + DatatypeConverter.printHexBinary(data));
            }
        }

    }

    private Optional<StateChangedEvent> toStateChangedEvent(RFXComMessageReceivedEvent event) {
        RFXComMessageConverter converter = RFXComMessageFactory.getConverter(event.getMessage());
        if (converter != null) {
            StateChangedEvent message = converter.decodeMessage(event.getMessage());
            return Optional.of(message);
        } else {
            return Optional.absent();
        }
    }

    public Observable<StateChangedEvent> stateChanges() {
        return stateChanges;
    }
}
