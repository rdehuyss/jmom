package org.jmom.core.model.eda;

import com.fasterxml.jackson.annotation.JsonCreator;
import org.jmom.core.infrastucture.eda.Event;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.typelibrary.StateChange;

public class StateChangedByInterfaceEvent extends Event {

    private DeviceIdentifier deviceIdentifier;
    private StateChange newState;

    protected StateChangedByInterfaceEvent() {

    }

    public StateChangedByInterfaceEvent(DeviceIdentifier deviceIdentifier, StateChange newState) {
        this.deviceIdentifier = deviceIdentifier;
        this.newState = newState;
    }

    public DeviceIdentifier getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public StateChange getNewState() {
        return newState;
    }

    @Override
    public String toString() {
        return "DeviceIdentifier: " + getDeviceIdentifier() + "; State: " + newState;
    }
}
