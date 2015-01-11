package org.jmom.interfaces.rfxcom.messages;

import org.jmom.core.model.eda.commands.ChangeStateCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.things.devices.typelibrary.CommandChange;
import org.jmom.core.model.things.devices.typelibrary.OnOffChange;
import org.jmom.core.model.things.devices.typelibrary.StateChange;
import org.jmom.interfaces.rfxcom.messages.types.PacketType;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;
import org.jmom.interfaces.rfxcom.messages.types.SubType;

public class RFXComLighting1MessageConverter implements RFXComMessageConverter {

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

    public enum Commands implements StateChange, CommandChange {
        OFF(0, OnOffChange.OFF),
        ON(1, OnOffChange.ON),
        DIM(2, OnOffChange.OFF),
        BRIGHT(3, OnOffChange.ON),
        GROUP_OFF(5, OnOffChange.OFF),
        GROUP_ON(6, OnOffChange.ON),
        CHIME(7, OnOffChange.ON),

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
    public byte[] encodeMessage(ChangeStateCommand message) {
        // Example data 	07 10 01 00 42 01 01 70
        //                  07 10 01 00 42 10 06 70

        RFXComIdentifier identifier = new RFXComIdentifier(message.getDeviceIdentifier());

        byte[] data = new byte[8];
        data[0] = 0x07;
        data[1] = PacketType.LIGHTING1.toByte();
        data[2] = identifier.getSubType(RFXComLighting1SubType.class).toByte();
        data[3] = (byte) 0;
        data[4] = identifier.getCharPartAsByte(3);
        data[5] = identifier.getIntPartAsByte(4);
        data[6] = Commands.valueOf(message.getCommandType().name()).toByte();
        data[7] = (byte) ((0 & 0x0F) << 4);
        return data;
    }

    @Override
    public StateChangedEvent decodeMessage(byte[] data) {
        RFXComIdentifier rfxComIdentifier = new RFXComIdentifier(
                PacketType.LIGHTING1,
                RFXComLighting1SubType.values()[data[2]],
                (char) data[4],
                (byte) data[5]
        );

        return new StateChangedEvent(rfxComIdentifier, Commands.values()[data[6]]);
    }
}