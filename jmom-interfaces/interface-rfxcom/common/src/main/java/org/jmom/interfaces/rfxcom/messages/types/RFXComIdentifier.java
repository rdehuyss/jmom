package org.jmom.interfaces.rfxcom.messages.types;

import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.interfaces.rfxcom.AbstractRFXComInterfaceProvider;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class RFXComIdentifier extends DeviceIdentifier {

    protected RFXComIdentifier() {}

    public RFXComIdentifier(DeviceIdentifier deviceIdentifier) {
        super(deviceIdentifier);
    }

    public RFXComIdentifier(String identifierAsString) {
        super(identifierAsString);
    }


    public RFXComIdentifier(PacketType packetType, SubType subType) {
        this(packetType, subType, null);
    }

    public RFXComIdentifier(PacketType packetType, SubType subType, Object... rest) {
        super(createParts(packetType, subType, rest));
    }


    public PacketType getPacketType() {
        return PacketType.valueOf(getPart(1));
    }

    public String getSubTypeAsString() {
        return getPart(2);
    }

    public <T extends Enum> T getSubType(Class<T> clazz) {
        return (T) Enum.valueOf(clazz, getSubTypeAsString());
    }

    private static List<String> createParts(PacketType packetType, SubType subType, Object[] rest) {
        List<String> parts = newArrayList(AbstractRFXComInterfaceProvider.NAME, packetType.name(), subType.name());
        if(rest != null) {
            for (Object o : rest) {
                parts.add(o.toString());
            }
        }
        return parts;
    }
}
