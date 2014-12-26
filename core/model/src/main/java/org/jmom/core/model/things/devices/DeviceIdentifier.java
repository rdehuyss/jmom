package org.jmom.core.model.things.devices;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public interface DeviceIdentifier {

    String getInterfaceName();

    String asString();

}
