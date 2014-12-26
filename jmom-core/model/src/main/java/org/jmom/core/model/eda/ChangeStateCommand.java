package org.jmom.core.model.eda;

import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.typelibrary.CommandChange;

public class ChangeStateCommand extends Command {

    private final DeviceIdentifier deviceIdentifier;
    private final CommandChange commandType;

    public ChangeStateCommand(DeviceIdentifier identifier, CommandChange commandType) {
        deviceIdentifier = identifier;
        this.commandType = commandType;
    }

    public DeviceIdentifier getDeviceIdentifier() {
        return deviceIdentifier;
    }

    public CommandChange getCommandType() {
        return commandType;
    }
}