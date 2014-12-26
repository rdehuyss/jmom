package org.jmom.core.model.things.devices;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class DeviceIdentifierImpl implements DeviceIdentifier {

    public static final String SEPARATOR = "-";
    private List<String> parts;

    protected DeviceIdentifierImpl() {

    }

    public DeviceIdentifierImpl(String identifierAsString) {
        parts = newArrayList(Splitter.on(SEPARATOR).split(identifierAsString));
    }

    @Override
    public String getInterfaceName() {
        return parts.get(0);
    }

    public String getPart(int index) {
        return parts.get(index).toString();
    }

    public char getPartAsChar(int index) {
        return getPart(index).charAt(0);
    }

    public byte getPartAsByte(int index) {
        return (byte) getPartAsInt(index);
    }

    public int getPartAsInt(int index) {
        return Integer.parseInt(getPart(index));
    }

    public int getPartsLength() {
        return parts.size();
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return toString().equals(obj.toString());
    }

    @Override
    public String toString() {
        return Joiner.on("-").join(parts);
    }

    @Override
    public String asString() {
        return toString();
    }
}
