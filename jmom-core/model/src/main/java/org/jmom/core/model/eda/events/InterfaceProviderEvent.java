package org.jmom.core.model.eda.events;

import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.infrastucture.eda.Event;
import org.jmom.core.model.controlunit.CentralControlUnit;

public class InterfaceProviderEvent extends Event {

    private CentralControlUnit centralControlUnit;

    protected InterfaceProviderEvent() {
        super();
    }

    public InterfaceProviderEvent(Command command) {
        super(command);
    }

    void setCentralControlUnit(CentralControlUnit id) {
        this.centralControlUnit = id;
    }

    public CentralControlUnit getCentralControlUnit() {
        return centralControlUnit;
    }
}
