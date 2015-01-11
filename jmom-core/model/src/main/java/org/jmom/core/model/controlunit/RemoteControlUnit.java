package org.jmom.core.model.controlunit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RemoteControlUnit extends ControlUnit {

    public RemoteControlUnit(String fqn) {
        super(fqn);
    }

    @JsonCreator
    public RemoteControlUnit(
            @JsonProperty("emailAddress") String emailAddress,
            @JsonProperty("password") String password,
            @JsonProperty("controlUnitName") String controlUnitName)  {
        super(emailAddress, password, controlUnitName);
    }

    @Override
    public boolean isCentralControlUnit() {
        return false;
    }

    @Override
    public boolean isRemoteControlUnit() {
        return true;
    }
}
