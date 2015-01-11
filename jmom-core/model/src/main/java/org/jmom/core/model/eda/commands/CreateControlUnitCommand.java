package org.jmom.core.model.eda.commands;

import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.model.controlunit.ControlUnit;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.typelibrary.CommandChange;

public class CreateControlUnitCommand extends Command {

    private ControlUnit controlUnit;

    protected CreateControlUnitCommand() {

    }

    public CreateControlUnitCommand(ControlUnit controlUnit) {
        this.controlUnit = controlUnit;
    }

    public ControlUnit getControlUnit() {
        return controlUnit;
    }
}
