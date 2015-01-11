package org.jmom.core.model.controlunit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CentralControlUnit extends ControlUnit {

    public CentralControlUnit(String fqn) {
        super(fqn);
    }

    @JsonCreator
    public CentralControlUnit(
            @JsonProperty("emailAddress") String emailAddress,
            @JsonProperty("password") String password,
            @JsonProperty("controlUnitName") String controlUnitName)  {
        super(emailAddress, password, controlUnitName);
    }

    @Override
    public boolean isCentralControlUnit() {
        return true;
    }

    @Override
    public boolean isRemoteControlUnit() {
        return false;
    }
}
