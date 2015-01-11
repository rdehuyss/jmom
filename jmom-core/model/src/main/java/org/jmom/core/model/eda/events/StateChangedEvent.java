package org.jmom.core.model.eda.events;

import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.typelibrary.StateChange;

public class StateChangedEvent extends InterfaceProviderEvent {

    private DeviceIdentifier deviceIdentifier;
    private StateChange newState;

    protected StateChangedEvent() {

    }

    public StateChangedEvent(DeviceIdentifier deviceIdentifier, StateChange newState) {
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
