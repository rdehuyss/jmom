package org.jmom.core.model.things;

import org.jmom.core.model.configuration.ResidenceConfiguration;

public class Residence extends Location {

    private ResidenceConfiguration residenceConfiguration;

    protected Residence() {
    }

    public Residence(String name) {
        super(name);
    }

    public Residence(String name, String description) {
        super(name, description);
    }

}
