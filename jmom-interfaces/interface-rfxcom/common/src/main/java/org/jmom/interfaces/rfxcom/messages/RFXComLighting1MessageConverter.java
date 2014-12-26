package org.jmom.interfaces.rfxcom.messages;

import com.google.common.collect.Sets;
import org.jmom.core.model.things.devices.typelibrary.AbstractChange;
import org.jmom.core.model.things.devices.typelibrary.OnOffChange;
import org.jmom.core.model.things.devices.typelibrary.StateChange;
import org.jmom.interfaces.rfxcom.messages.types.PacketType;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;
import org.jmom.interfaces.rfxcom.messages.types.SubType;

import java.util.Set;

public class RFXComLighting1MessageConverter implements RFXComMessageConverter<RFXComLighting1MessageConverter.RFXComLighting1Message> {

    public enum RFXComLighting1SubType implements SubType {
        X10(0),
        ARC(1),
        AB400D(2),
        WAVEMAN(3),
        EMW200(4),
        IMPULS(5),
        RISINGSUN(6),
        PHILIPS(7),
        ENERGENIE(8),

        UNKNOWN(255);

        private final int subType;

        RFXComLighting1SubType(int subType) {
            this.subType = subType;
        }

        RFXComLighting1SubType(byte subType) {
            this.subType = subType;
        }

        public byte toByte() {
            return (byte) subType;
        }
    }

    public enum Commands {
        OFF(0, OnOffChange.OFF),
        ON(1, OnOffChange.ON),
        DIM(2, OnOffChange.OFF),
        BRIGHT(3, OnOffChange.ON),
        GROUP_OFF(5, OnOffChange.OFF),
        GROUP_ON(6, OnOffChange.ON),
        CHIME(7, OnOffChange.OFF),

        UNKNOWN(255, null);

        private final int command;
        private final OnOffChange onOffChange;

        Commands(int command, OnOffChange onOffChange) {
            this.command = command;
            this.onOffChange = onOffChange;
        }

        public byte toByte() {
            return (byte) command;
        }

        public OnOffChange getOnOffChange() {
            return onOffChange;
        }
    }

    @Override
    public byte[] encodeMessage(RFXComLighting1Message rfxComLighting1Message) {
        // Example data 	07 10 01 00 42 01 01 70
        //                  07 10 01 00 42 10 06 70

        byte[] data = new byte[8];
        data[0] = 0x07;
        data[1] = PacketType.LIGHTING1.toByte();
        data[2] = rfxComLighting1Message.getIdentifier().getSubType(RFXComLighting1SubType.class).toByte();
        data[3] = 0;
        data[4] = rfxComLighting1Message.getIdentifier().getPartAsByte(3);
        data[5] = rfxComLighting1Message.getIdentifier().getPartAsByte(4);
        data[6] = rfxComLighting1Message.getCommand().toByte();
        data[7] = (byte) ((rfxComLighting1Message.getSignalLevel() & 0x0F) << 4);
        return data;
    }

    @Override
    public RFXComLighting1Message decodeMessage(byte[] data) {
        RFXComLighting1Identifier rfxComIdentifier = new RFXComLighting1Identifier(
                PacketType.LIGHTING1,
                RFXComLighting1SubType.values()[data[2]],
                (char) data[4],
                (byte) data[5]
        );

        return new RFXComLighting1Message(rfxComIdentifier,
                Commands.values()[data[6]],
                (byte) ((data[7] & 0xF0) >> 4)
        );
    }

    public static class RFXComLighting1Message extends RFXComBaseStateChangeMessage<RFXComLighting1Identifier> {

        private final Commands command;
        private final byte signalLevel;

        public RFXComLighting1Message(RFXComLighting1Identifier identifier, Commands command) {
            this(identifier, command, (byte) 0);
        }

        public RFXComLighting1Message(RFXComLighting1Identifier identifier, Commands command, byte signalLevel) {
            super(identifier);
            this.command = command;
            this.signalLevel = signalLevel;
        }

        public Commands getCommand() {
            return command;
        }

        public byte getSignalLevel() {
            return signalLevel;
        }

        @Override
        public String toString() {
            String str = "";
            str += "RFXCom Message";
            str += "\n - Packet type = " + getIdentifier().getPacketType();
            str += "\n - Sub type = " + getIdentifier().getSubTypeAsString();
            str += "\n - Id = " + getIdentifier().getPart(3);
            str += "\n - Unit code = " + getIdentifier().getPart(4);
            str += "\n - Command = " + command;
            str += "\n - Signal level = " + signalLevel;
            return str;
        }

        @Override
        public StateChange getStateChange() {
            return command.getOnOffChange();
        }
    }

    public static class RFXComLighting1Identifier extends RFXComIdentifier {

        protected RFXComLighting1Identifier() {}

        public RFXComLighting1Identifier(String identifierAsString) {
            super(identifierAsString);
        }

        public RFXComLighting1Identifier(PacketType packetType, SubType subType, char id, byte unitCode) {
            super(packetType, subType, id, unitCode);
        }

        public RFXComLighting1SubType getSubType() {
            return getSubType(RFXComLighting1SubType.class);
        }

        public char getLetterCode() {
            return getPartAsChar(3);
        }

        public byte getUnitCode() {
            return getPartAsByte(4);
        }
    }
}