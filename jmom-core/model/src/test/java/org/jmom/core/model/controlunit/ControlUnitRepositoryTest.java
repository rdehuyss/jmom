package org.jmom.core.model.controlunit;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.assertj.core.api.Assertions;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.eda.commands.CreateControlUnitCommand;
import org.junit.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class ControlUnitRepositoryTest {

    private ControlUnitRepository repository = new ControlUnitRepository();
    private JMomObjectMapper jMomObjectMapper = new JMomObjectMapper();

    @Test
    public void serialize() throws IOException {
        CreateControlUnitCommand command = new CreateControlUnitCommand(new CentralControlUnit("ronald.dehuysser@gmail.com", "testen", "Central Control Unit"));
        repository.apply(new ControlUnitRepository.ControlUnitCreatedDomainEvent(command));

        String repositoryAsString = jMomObjectMapper.writeValueAsString(repository);

        ControlUnitRepository actual = jMomObjectMapper.readValue(repositoryAsString, ControlUnitRepository.class);

        assertThat(actual.getControlUnit()).isNotNull();
    }

}