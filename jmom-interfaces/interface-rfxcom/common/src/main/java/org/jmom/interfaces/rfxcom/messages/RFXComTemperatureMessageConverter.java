package org.jmom.interfaces.rfxcom.messages;

import com.google.common.collect.Sets;
import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.core.model.things.devices.typelibrary.AbstractChange;
import org.jmom.core.model.things.devices.typelibrary.StateChange;
import org.jmom.interfaces.rfxcom.messages.types.PacketType;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;
import org.jmom.interfaces.rfxcom.messages.types.SubType;

import java.math.BigDecimal;
import java.util.Set;

public class RFXComTemperatureMessageConverter implements RFXComMessageConverter {

    public enum RFXComTemperatureSubType implements SubType {
        UNDEF(0),
        THR128_138_THC138(1),
        THC238_268_THN122_132_THWR288_THRN122_AW129_131(2),
        THWR800(3),
        RTHN318(4),
        LACROSSE_TX2_TX3_TX4_TX17(5),
        TS15C(6),
        VIKING_02811(7),
        LACROSSE_WS2300(8),
        RUBICSON(9),
        TFA_30_3133(10),

        UNKNOWN(255);

        private final int subType;

        RFXComTemperatureSubType(int subType) {
            this.subType = subType;
        }

        public byte toByte() {
            return (byte) subType;
        }
    }

    @Override
    public byte[] encodeMessage(ChangeStateCommand rfxComTemperatureMessage) {
        throw new IllegalStateException("We can not control the temperature via RFXCom");
    }

    @Override
    public StateChangedEvent decodeMessage(byte[] data) {
        RFXComIdentifier rfxComIdentifier = new RFXComIdentifier(
                PacketType.TEMPERATURE,
                RFXComTemperatureSubType.values()[data[2]],
                (data[4] & 0xFF) << 8 | (data[5] & 0xFF)
        );

        return new StateChangedEvent(
                rfxComIdentifier,
                new TemperatureStateChange((short) ((data[6] & 0x7F) << 8 | (data[7] & 0xFF)) * 0.1)
        );

//        return new RFXComTemperatureMessage(rfxComIdentifier,
//                (short) ((data[6] & 0x7F) << 8 | (data[7] & 0xFF)) * 0.1,
//                (byte) ((data[8] & 0xF0) >> 4),
//                (byte) (data[8] & 0x0F)
//        );
    }

    public static class TemperatureStateChange implements StateChange {

        private BigDecimal value;

        protected TemperatureStateChange() {}

        public TemperatureStateChange(double value) {
            this.value = new BigDecimal(value).setScale(2, BigDecimal.ROUND_DOWN);
        }

        @Override
        public String name() {
            return "Temperature";
        }

        public BigDecimal getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "Temperature = " + value.toString();
        }
    }
}