package org.jmom.core.model.things.devices;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Predicate;
import org.jmom.core.model.things.Thing;
import org.jmom.core.model.things.devices.typelibrary.AbstractChange;

import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public abstract class Device<S extends Device, T extends AbstractChange> extends Thing<S> {

    private Set<DeviceIdentifier> identifiers = newHashSet();
    @JsonIgnore
    private T state;

    public Device(String name) {
        super(name);
    }

    protected Device() {
    }

    public Set<DeviceIdentifier> getIdentifiers() {
        return identifiers;
    }

    public S addIdentifier(org.jmom.core.model.things.devices.DeviceIdentifier deviceIdentifier) {
        identifiers.add(deviceIdentifier);
        return (S) this;
    }

    public T getState() {
        return state;
    }

    public S setState(T state) {
        this.state = state;
        return (S) this;
    }

    public static Predicate<Device> hasDeviceIdentifier(final DeviceIdentifier identifier) {
        return input -> input.identifiers.contains(identifier);
    }
}
