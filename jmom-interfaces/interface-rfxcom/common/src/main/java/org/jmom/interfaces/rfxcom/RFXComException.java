package org.jmom.interfaces.rfxcom;

public class RFXComException extends Exception {

    public RFXComException() {
        super();
    }

    public RFXComException(String message) {
        super(message);
    }

    public RFXComException(String message, Throwable cause) {
        super(message, cause);
    }

    public RFXComException(Throwable cause) {
        super(cause);
    }

}