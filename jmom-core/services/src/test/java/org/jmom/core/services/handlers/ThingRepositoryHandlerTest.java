package org.jmom.core.services.handlers;

import com.google.common.base.Optional;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.eda.commands.SaveThingCommand;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.Residence;
import org.jmom.core.model.things.Thing;
import org.jmom.core.model.things.ThingRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jmom.core.model.things.Path.root;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ThingRepositoryHandlerTest {

    private ThingRepositoryHandler handler;

    private ThingRepository thingRepository;
    @Mock
    private Repo repo;

    private JMomBus jMomBus;

    @Before
    public void initThingRepositoryHandler() {
        thingRepository = new ThingRepository();
        handler = new ThingRepositoryHandler(thingRepository, repo);
        jMomBus = new JMomBus();
        jMomBus.register(handler);
    }

    @Test
    public void handleSaveThingCommand() throws IOException {
        Residence residence = new Residence("Thuis", "Pastorijstraat 150, 3300 Tienen");

        jMomBus.post(new SaveThingCommand(root(), residence));

        Optional<Thing> actual = thingRepository.getByPath(Path.fromString("/Thuis"));
        assertThat(actual.isPresent()).isTrue();
        assertThat(actual.get()).isEqualTo(residence);
        verify(repo).save(thingRepository);
    }

}