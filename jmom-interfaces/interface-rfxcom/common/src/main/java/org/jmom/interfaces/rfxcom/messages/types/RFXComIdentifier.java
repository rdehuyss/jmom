package org.jmom.interfaces.rfxcom.messages.types;

import com.google.common.base.Joiner;
import org.jmom.core.model.things.devices.DeviceIdentifierImpl;
import org.jmom.interfaces.rfxcom.AbstractRFXComInterfaceProvider;

public class RFXComIdentifier extends DeviceIdentifierImpl {

    protected RFXComIdentifier() {}

    public RFXComIdentifier(String identifierAsString) {
        super(identifierAsString);
    }

    public RFXComIdentifier(PacketType packetType, SubType subType) {
        super(Joiner.on(SEPARATOR).join(AbstractRFXComInterfaceProvider.NAME, packetType, subType));
    }

    public RFXComIdentifier(PacketType packetType, SubType subType, Object... rest) {
        super(Joiner.on(SEPARATOR).join(AbstractRFXComInterfaceProvider.NAME, packetType, subType, Joiner.on(SEPARATOR).join(rest)));
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
}
