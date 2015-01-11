package org.jmom.core.model.eda.commands;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.things.Residence;

public class LinkCentralControlUnitToResidenceCommand extends Command {

    private final CentralControlUnit centralControlUnit;
    private final Residence residence;

    @JsonCreator
    public LinkCentralControlUnitToResidenceCommand(@JsonProperty("centralControlUnit") CentralControlUnit centralControlUnit, @JsonProperty("residence") Residence residence) {
        this.centralControlUnit = centralControlUnit;
        this.residence = residence;
    }

    public Residence getResidence() {
        return residence;
    }

    public CentralControlUnit getCentralControlUnit() {
        return centralControlUnit;
    }
}
