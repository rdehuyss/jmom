package org.jmom.interfaces.rfxcom.messages;

import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.interfaces.rfxcom.RFXComException;
import org.jmom.interfaces.rfxcom.messages.types.PacketType;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.jmom.interfaces.rfxcom.messages.types.PacketType.getPacketTypeByByte;

public class RFXComMessageFactory {

    @SuppressWarnings("serial")
    private static final Map<PacketType, RFXComMessageConverter> messageClasses =
            Collections
            .unmodifiableMap(new HashMap<PacketType, RFXComMessageConverter>() {
                {
//                    put(PacketType.INTERFACE_CONTROL, "RFXComControlMessage");
                    put(PacketType.INTERFACE_MESSAGE, new RFXComInterfaceMessageConverter());
//                    put(PacketType.TRANSMITTER_MESSAGE, "RFXComTransmitterMessage");
//                    put(PacketType.UNDECODED_RF_MESSAGE, "RFXComUndecodedRFMessage");
                    put(PacketType.LIGHTING1, new RFXComLighting1MessageConverter());
                    put(PacketType.TEMPERATURE, new RFXComTemperatureMessageConverter());

                }
            });

    /**
     * Command to reset RFXCOM controller.
     *
     */
    public final static byte[] CMD_RESET = new byte[] { 0x0D, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    /**
     * Command to get RFXCOM controller status.
     *
     */
    public final static byte[] CMD_STATUS = new byte[] { 0x0D, 0x00, 0x00,
            0x01, 0x02, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

    /**
     * Command to save RFXCOM controller configuration.
     *
     */
    public final static byte[] CMD_SAVE = new byte[] { 0x0D, 0x00, 0x00, 0x00,
            0x06, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };




    public static RFXComMessageConverter getConverter(byte[] packet) {
        PacketType packetType = getPacketTypeByByte(packet[1]);
        return getConverter(packetType);
    }

    public static RFXComMessageConverter getConverter(ChangeStateCommand command) {
        return getConverter(new RFXComIdentifier(command.getDeviceIdentifier()).getPacketType());
    }

    private static RFXComMessageConverter getConverter(PacketType packetType) {
        return messageClasses.get(packetType);
    }


}