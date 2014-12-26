package org.jmom.core.model.things.devices;

import org.jmom.core.model.things.devices.typelibrary.OnOffChange;

public class Light extends Device<Light, OnOffChange> {

    protected Light() {}

    public Light(String name) {
        super(name);
    }
}
