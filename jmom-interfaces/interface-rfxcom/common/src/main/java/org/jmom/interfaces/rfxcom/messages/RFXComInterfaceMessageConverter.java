package org.jmom.interfaces.rfxcom.messages;

import com.google.common.collect.Sets;
import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.core.model.things.devices.typelibrary.AbstractChange;
import org.jmom.interfaces.rfxcom.messages.types.PacketType;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;
import org.jmom.interfaces.rfxcom.messages.types.SubType;

import java.util.Set;

public class RFXComInterfaceMessageConverter implements RFXComMessageConverter {

    public enum RFXComInterfaceSubType implements SubType {
        INTERFACE_RESPONSE(0),

        UNKNOWN(255);

        private final int subType;

        RFXComInterfaceSubType(int subType) {
            this.subType = subType;
        }

        RFXComInterfaceSubType(byte subType) {
            this.subType = subType;
        }

        public byte toByte() {
            return (byte) subType;
        }
    }

    public enum Commands {
        RESET(0),					// Reset the receiver/transceiver. No answer is transmitted!
        NOT_USED1(1),				// Not used
        GET_STATUS(2),				// Get Status, return firmware versions and configuration of the interface
        SET_MODE(3),				// Set mode msg1-msg5, return firmware versions and configuration of the interface
        ENABLE_ALL(4),				// Enable all receiving modes of the receiver/transceiver
        ENABLE_UNDECODED_PACKETS(5),// Enable reporting of undecoded packets
        SAVE_RECEIVING_MODES(6),	// Save receiving modes of the receiver/transceiver in non-volatile memory
        NOT_USED7(7),				// Not used
        T1(8),						// For internal use by RFXCOM
        T2(9),						// For internal use by RFXCOM

        UNKNOWN(255);

        private final int command;

        Commands(int command) {
            this.command = command;
        }

        Commands(byte command) {
            this.command = command;
        }

        public byte toByte() {
            return (byte) command;
        }
    }

    public enum TransceiverType {
        _310MHZ(80),
        _315MHZ(81),
        _443_92MHZ_RECEIVER_ONLY(82),
        _443_92MHZ_TRANSCEIVER(83),
        _868_00MHZ(85),
        _868_00MHZ_FSK(86),
        _868_30MHZ(87),
        _868_30MHZ_FSK(88),
        _868_35MHZ(89),
        _868_35MHZ_FSK(90),
        _868_95MHZ_FSK(91),

        UNKNOWN(255);

        private final int type;

        TransceiverType(int type) {
            this.type = type;
        }

        TransceiverType(byte type) {
            this.type = type;
        }

        public byte toByte() {
            return (byte) type;
        }
    }

    @Override
    public StateChangedEvent decodeMessage(byte[] data) {
        RFXComInterfaceIdentifier rfxComIdentifier = new RFXComInterfaceIdentifier(
                PacketType.INTERFACE_MESSAGE,
                RFXComInterfaceSubType.values()[data[2]]);

        return new RFXComInterfaceMessage(rfxComIdentifier, data);
    }

    @Override
    public byte[] encodeMessage(ChangeStateCommand message) {
        return new byte[0];
    }

    public static class RFXComInterfaceMessage extends StateChangedEvent {

        private TransceiverType transceiverType = TransceiverType._443_92MHZ_TRANSCEIVER;
        private Commands command = Commands.UNKNOWN;
        private byte firmwareVersion = 0;

        private boolean enableUndecodedPackets = false;	// 0x80 - Undecoded packets
        private boolean enableRFU6Packets = false;		// 0x40 - RFU6
        private boolean enableRFU5Packets = false;		// 0x20 - RFU5
        private boolean enableRFU4Packets = false;		// 0x10 - RFU4
        private boolean enableRFU3Packets = false;		// 0x08 - RFU3
        private boolean enableFineOffsetPackets = false;	// 0x04 - FineOffset / Viking (433.92)
        private boolean enableRubicsonPackets = false;	// 0x02 - Rubicson (433.92)
        private boolean enableAEPackets = false;			// 0x01 - AE (433.92)

        private boolean enableBlindsT1Packets = false;	// 0x80 - BlindsT1/T2/T3 (433.92)
        private boolean enableBlindsT0Packets = false;	// 0x40 - BlindsT0 (433.92)
        private boolean enableProGuardPackets = false;	// 0x20 - ProGuard (868.35 FSK)
        private boolean enableFS20Packets = false;		// 0x10 - FS20 (868.35)
        private boolean enableLaCrossePackets = false;	// 0x08 - La Crosse (433.92/868.30)
        private boolean enableHidekiPackets = false;		// 0x04 - Hideki/UPM (433.92)
        private boolean enableADPackets = false;			// 0x02 - AD (433.92)
        private boolean enableMertikPackets = false;		// 0x01 - Mertik (433.92)

        private boolean enableVisonicPackets = false;	// 0x80 - Visonic (315/868.95)
        private boolean enableATIPackets = false;		// 0x40 - ATI (433.92)
        private boolean enableOregonPackets = false;		// 0x20 - Oregon Scientific (433.92)
        private boolean enableMeiantechPackets = false;	// 0x10 - Meiantech (433.92)
        private boolean enableHomeEasyPackets = false;	// 0x08 - HomeEasy EU (433.92)
        private boolean enableACPackets = false;			// 0x04 - AC (433.92)
        private boolean enableARCPackets = false;		// 0x02 - ARC (433.92)
        private boolean enableX10Packets = false;		// 0x01 - X10 (310/433.92)

        private byte hardwareVersion1  = 0;
        private byte hardwareVersion2  = 0;

        protected RFXComInterfaceMessage(RFXComInterfaceIdentifier identifier, byte[] data) {
            super(identifier, null);
            try {
                command = Commands.values()[data[4]];
            } catch (Exception e) {
                command = Commands.UNKNOWN;
            }

            transceiverType = TransceiverType.UNKNOWN;

            for (TransceiverType type : TransceiverType.values()) {
                if (type.toByte() == data[5]) {
                    transceiverType = type;
                    break;
                }
            }

            firmwareVersion = data[6];

            enableUndecodedPackets 	= (data[7] & 0x80) != 0 ? true : false;
            enableRFU6Packets 		= (data[7] & 0x40) != 0 ? true : false;
            enableRFU5Packets 		= (data[7] & 0x20) != 0 ? true : false;
            enableRFU4Packets 		= (data[7] & 0x10) != 0 ? true : false;
            enableRFU3Packets 		= (data[7] & 0x08) != 0 ? true : false;
            enableFineOffsetPackets = (data[7] & 0x04) != 0 ? true : false;
            enableRubicsonPackets 	= (data[7] & 0x02) != 0 ? true : false;
            enableAEPackets 		= (data[7] & 0x01) != 0 ? true : false;

            enableBlindsT1Packets 	= (data[8] & 0x80) != 0 ? true : false;
            enableBlindsT0Packets 	= (data[8] & 0x40) != 0 ? true : false;
            enableProGuardPackets 	= (data[8] & 0x20) != 0 ? true : false;
            enableFS20Packets 		= (data[8] & 0x10) != 0 ? true : false;
            enableLaCrossePackets 	= (data[8] & 0x08) != 0 ? true : false;
            enableHidekiPackets 	= (data[8] & 0x04) != 0 ? true : false;
            enableADPackets 		= (data[8] & 0x02) != 0 ? true : false;
            enableMertikPackets 	= (data[8] & 0x01) != 0 ? true : false;

            enableVisonicPackets 	= (data[9] & 0x80) != 0 ? true : false;
            enableATIPackets 		= (data[9] & 0x40) != 0 ? true : false;
            enableOregonPackets 	= (data[9] & 0x20) != 0 ? true : false;
            enableMeiantechPackets 	= (data[9] & 0x10) != 0 ? true : false;
            enableHomeEasyPackets 	= (data[9] & 0x08) != 0 ? true : false;
            enableACPackets 		= (data[9] & 0x04) != 0 ? true : false;
            enableARCPackets 		= (data[9] & 0x02) != 0 ? true : false;
            enableX10Packets 		= (data[9] & 0x01) != 0 ? true : false;

            hardwareVersion1 = data[10];
            hardwareVersion2 = data[11];
        }


        @Override
        public String toString() {
            String str = "";
            str += "RFXCom Message";
            str += "\n - Packet type = " + ((RFXComInterfaceIdentifier)getDeviceIdentifier()).getPacketType();
            str += "\n - Sub type = " + ((RFXComInterfaceIdentifier)getDeviceIdentifier()).getSubType();
            str += "\n - Command = " + command;
            str += "\n - Transceiver type = " + transceiverType;
            str += "\n - Firmware version = " + firmwareVersion;
            str += "\n - Hardware version = " + hardwareVersion1 + "." + hardwareVersion2;
            str += "\n - Undecoded packets = " + enableUndecodedPackets;
            str += "\n - RFU6 packets = " + enableRFU6Packets;
            str += "\n - RFU5 packets = " + enableRFU5Packets;
            str += "\n - RFU4 packets = " + enableRFU4Packets;
            str += "\n - RFU3 packets = " + enableRFU3Packets;
            str += "\n - FineOffset / Viking (433.92) packets = " + enableFineOffsetPackets;
            str += "\n - Rubicson (433.92) packets = " + enableRubicsonPackets;
            str += "\n - AE (433.92) packets = " + enableAEPackets;

            str += "\n - BlindsT1/T2/T3 (433.92) packets = " + enableBlindsT1Packets;
            str += "\n - BlindsT0 (433.92) packets = " + enableBlindsT0Packets;
            str += "\n - ProGuard (868.35 FSK) packets = " + enableProGuardPackets;
            str += "\n - FS20 (868.35) packets = " + enableFS20Packets;
            str += "\n - La Crosse (433.92/868.30) packets = " + enableLaCrossePackets;
            str += "\n - Hideki/UPM (433.92) packets = " + enableHidekiPackets;
            str += "\n - AD (433.92) packets = " + enableADPackets;
            str += "\n - Mertik (433.92) packets = " + enableMertikPackets;

            str += "\n - Visonic (315/868.95) packets = " + enableVisonicPackets;
            str += "\n - ATI (433.92) packets = " + enableATIPackets;
            str += "\n - Oregon Scientific (433.92) packets = " + enableOregonPackets;
            str += "\n - Meiantech (433.92) packets = " + enableMeiantechPackets;
            str += "\n - HomeEasy EU (433.92) packets = " + enableHomeEasyPackets;
            str += "\n - AC (433.92) packets = " + enableACPackets;
            str += "\n - ARC (433.92) packets = " + enableARCPackets;
            str += "\n - X10 (310/433.92) packets = " + enableX10Packets;

            return str;
        }
    }

    public static class RFXComInterfaceIdentifier extends RFXComIdentifier {

        public RFXComInterfaceIdentifier(PacketType packetType, SubType subType) {
            super(packetType, subType);
        }

        public RFXComInterfaceSubType getSubType() {
            return getSubType(RFXComInterfaceSubType.class);
        }
    }
}
