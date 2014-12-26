package org.jmom.core.model.things.devices;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class DeviceIdentifier implements Iterable<String> {

    public static final String SEPARATOR = "-";
    private List<String> parts;

    protected DeviceIdentifier() {

    }

    protected DeviceIdentifier(String... partObjects) {
        this.parts = newArrayList(partObjects);
    }

    protected DeviceIdentifier(List<String> parts) {
        this.parts = parts;
    }

    protected DeviceIdentifier(DeviceIdentifier deviceIdentifier) {
        this.parts = deviceIdentifier.parts;
    }

    public DeviceIdentifier(String identifierAsString) {
        this.parts = newArrayList(Splitter.on(SEPARATOR).split(identifierAsString));
    }

    public String getInterfaceName() {
        return parts.get(0);
    }

    public String getPart(int index) {
        return parts.get(index).toString();
    }

    public char getPartAsChar(int index) {
        return getPart(index).charAt(0);
    }

    public byte getCharPartAsByte(int index) {
        return (byte) getPartAsChar(index);
    }

    public byte getIntPartAsByte(int index) {
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

    public String asString() {
        return toString();
    }

    @Override
    public Iterator<String> iterator() {
        return parts.iterator();
    }
}
