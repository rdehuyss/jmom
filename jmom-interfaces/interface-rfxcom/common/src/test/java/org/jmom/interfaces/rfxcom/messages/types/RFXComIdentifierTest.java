package org.jmom.interfaces.rfxcom.messages.types;

import org.jmom.interfaces.rfxcom.AbstractRFXComInterfaceProvider;
import org.jmom.interfaces.rfxcom.messages.RFXComLighting1MessageConverter.RFXComLighting1SubType;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmom.interfaces.rfxcom.messages.RFXComLighting1MessageConverter.RFXComLighting1SubType.X10;

public class RFXComIdentifierTest {

    @Test
    public void testToString() {
        String result = new RFXComIdentifier(PacketType.LIGHTING1, X10, 'A', (byte) 3).toString();
        String expected = "RFXCom-LIGHTING1-X10-A-3";
        assertThat(result).isEqualTo(expected);
    }

    @Test
    public void testFromString() {
        RFXComIdentifier result = new RFXComIdentifier("RFXCom-LIGHTING1-X10-A-3");

        assertThat(result.getPartsLength()).isEqualTo(5);
        assertThat(result.getInterfaceName()).isEqualTo(AbstractRFXComInterfaceProvider.NAME);
        assertThat(result.getPacketType()).isEqualTo(PacketType.LIGHTING1);
        assertThat(result.getSubType(RFXComLighting1SubType.class)).isEqualTo(X10);
        assertThat(result.getPartAsChar(3)).isEqualTo('A');
        assertThat(result.getCharPartAsByte(4)).isEqualTo((byte) 3);
    }

    @Test
    public void testEquals() {
        RFXComIdentifier obj1 = new RFXComIdentifier(PacketType.LIGHTING1, X10, 'A', (byte) 3);
        RFXComIdentifier obj2 = new RFXComIdentifier("RFXCom-LIGHTING1-X10-A-3");
        assertThat(obj1).isEqualTo(obj2);
    }
}