package org.jmom.core.model.eda.commands;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.Assertions;
import org.jmom.core.infrastucture.eda.Message;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.Residence;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class SaveThingCommandTest {

    private JMomObjectMapper jMomObjectMapper = new JMomObjectMapper();

    @Test
    public void canBeSerializedAndDeserialized() throws IOException {
        Residence residence = new Residence("Home", "Pastorijstraat 150");
        SaveThingCommand saveThingCommand = new SaveThingCommand(Path.root(), residence);

        String asString = jMomObjectMapper.writeValueAsString(saveThingCommand);
        Message message = jMomObjectMapper.readValue(asString, Message.class);
    
        assertThat(message).isInstanceOf(SaveThingCommand.class);
    }

}