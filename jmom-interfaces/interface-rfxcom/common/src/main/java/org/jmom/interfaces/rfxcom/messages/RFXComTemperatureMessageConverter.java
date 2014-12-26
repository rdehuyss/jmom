package org.jmom.interfaces.rfxcom.messages;

import com.google.common.collect.Sets;
import org.jmom.core.model.things.devices.typelibrary.AbstractChange;
import org.jmom.core.model.things.devices.typelibrary.StateChange;
import org.jmom.interfaces.rfxcom.messages.types.PacketType;
import org.jmom.interfaces.rfxcom.messages.types.RFXComIdentifier;
import org.jmom.interfaces.rfxcom.messages.types.SubType;

import java.math.BigDecimal;
import java.util.Set;

public class RFXComTemperatureMessageConverter implements RFXComMessageConverter<RFXComTemperatureMessageConverter.RFXComTemperatureMessage> {

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

        RFXComTemperatureSubType(byte subType) {
            this.subType = subType;
        }

        public byte toByte() {
            return (byte) subType;
        }
    }

    @Override
    public byte[] encodeMessage(RFXComTemperatureMessage rfxComTemperatureMessage) {
        throw new IllegalStateException("We can not control the temperature via RFXCom");
    }

    @Override
    public RFXComTemperatureMessage decodeMessage(byte[] data) {
        RFXComTemperatureIdentifier rfxComIdentifier = new RFXComTemperatureIdentifier(
                PacketType.TEMPERATURE,
                RFXComTemperatureSubType.values()[data[2]],
                (data[4] & 0xFF) << 8 | (data[5] & 0xFF)
        );

        return new RFXComTemperatureMessage(rfxComIdentifier,
                (short) ((data[6] & 0x7F) << 8 | (data[7] & 0xFF)) * 0.1,
                (byte) ((data[8] & 0xF0) >> 4),
                (byte) (data[8] & 0x0F)
        );
    }

    public static class RFXComTemperatureMessage extends RFXComBaseStateChangeMessage<RFXComTemperatureIdentifier> {

        private final double temperature;
        private final byte signalLevel;
        private final byte batteryLevel;

        public RFXComTemperatureMessage(RFXComTemperatureIdentifier identifier, double temperature, byte signalLevel, byte batteryLevel) {
            super(identifier);
            this.temperature = temperature;
            this.signalLevel = signalLevel;
            this.batteryLevel = batteryLevel;
        }

        public double getTemperature() {
            return temperature;
        }

        public byte getSignalLevel() {
            return signalLevel;
        }

        public byte getBatteryLevel() {
            return batteryLevel;
        }

        @Override
        public String toString() {
            String str = "";
            str += "RFXCom Message";
            str += "\n - Packet type = " + getIdentifier().getPacketType();
            str += "\n - Sub type = " + getIdentifier().getSubTypeAsString();
            str += "\n - Sensor Id = " + getIdentifier().getSensorId();
            str += "\n - Temperature = " + temperature;
            str += "\n - Signal level = " + signalLevel;
            str += "\n - Battery level = " + batteryLevel;
            return str;
        }

        @Override
        public StateChange getStateChange() {
            return new TemperatureStateChange(temperature);
        }
    }

    public static class RFXComTemperatureIdentifier extends RFXComIdentifier {

        protected RFXComTemperatureIdentifier() {}

        public RFXComTemperatureIdentifier(String identifierAsString) {
            super(identifierAsString);
        }

        public RFXComTemperatureIdentifier(PacketType packetType, SubType subType, int sensorId) {
            super(packetType, subType, sensorId);
        }

        public RFXComTemperatureSubType getSubType() {
            return getSubType(RFXComTemperatureSubType.class);
        }

        public int getSensorId() {
            return getPartAsInt(3);
        }

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