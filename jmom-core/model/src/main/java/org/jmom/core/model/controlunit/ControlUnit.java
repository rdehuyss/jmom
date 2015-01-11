package org.jmom.core.model.controlunit;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Objects;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class ControlUnit {

    private final String emailAddress;
    private final String password;
    private final String controlUnitName;

    protected ControlUnit(String fqn) {
        String[] splitted = fqn.split("/");
        String [] emailAddressAndPassword = splitted[0].split(":");
        emailAddress = emailAddressAndPassword[0];
        password = emailAddressAndPassword[1];
        controlUnitName = splitted[1];
    }

    protected ControlUnit(String emailAddress, String password, String controlUnitName) {
        this.emailAddress = emailAddress;
        this.password = password;
        this.controlUnitName = controlUnitName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getControlUnitName() {
        return controlUnitName;
    }

    public String getFQN() {
        return getEmailAddress() + ":" + getPassword() + "/" + getControlUnitName();
    }

    public String getFQNWithoutPassword() {
        return getEmailAddress() + "/" + getControlUnitName();
    }

    public abstract boolean isCentralControlUnit();

    public abstract boolean isRemoteControlUnit();

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ControlUnit) {
            ControlUnit that = (ControlUnit) obj;
            return Objects.equals(this.emailAddress, that.emailAddress)
                    && Objects.equals(this.password, that.password)
                    && Objects.equals(this.controlUnitName, that.controlUnitName);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.emailAddress, this.password, this.controlUnitName);
    }

    @Override
    public String toString() {
        return getFQN();
    }
}
