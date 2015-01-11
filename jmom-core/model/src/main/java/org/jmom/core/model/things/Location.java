package org.jmom.core.model.things;

public class Location extends ThingTree<Location> {

    protected Location() {}

    public Location(String name) {
        super(name);
    }

    public Location(String name, String description) {
        super(name, description);
    }
}
