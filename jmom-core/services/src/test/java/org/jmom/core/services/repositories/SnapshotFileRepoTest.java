package org.jmom.core.services.repositories;

import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.eda.SaveThingCommand;
import org.jmom.core.model.things.Location;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.ThingRepository;
import org.jmom.core.model.things.ThingRepository.UpdateOrSaveThingDomainEvent;
import org.jmom.core.model.things.devices.Light;
import org.jmom.core.model.things.devices.typelibrary.OnOffChange;
import org.jmom.core.services.handlers.ThingRepositoryHandler;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

public class SnapshotFileRepoTest {

    private SnapshotFileRepo snapshotFileRepo;

    private ThingRepository thingRepository = new ThingRepository();
    private ThingRepositoryHandler commandHandler;

    private Location house;
    private Location gelijkvloers;
    private Location eersteVerdieping;
    private Location living;
    private Location keuken;
    private Location eetkamer;
    private Light spots;

    @Before
    public void setUpEnv() throws IOException {
        house = new Location("House Pastorijstraat");
        gelijkvloers = new Location("Gelijkvloers");
        eersteVerdieping = new Location("Eerste verdieping");
        living = new Location("Living");
        keuken = new Location("Keuken");
        eetkamer = new Location("Eetkamer");
        spots = new Light("Spots")
                .withAttribute("identifier", "id-1")
                .setState(OnOffChange.ON);

        commandHandler.handle(new SaveThingCommand(Path.root(), house));
        commandHandler.handle(new SaveThingCommand(house, gelijkvloers));
        commandHandler.handle(new SaveThingCommand(house, eersteVerdieping));
        commandHandler.handle(new SaveThingCommand(gelijkvloers, living));
        commandHandler.handle(new SaveThingCommand(gelijkvloers, keuken));
        commandHandler.handle(new SaveThingCommand(gelijkvloers, eetkamer));
        commandHandler.handle(new SaveThingCommand(living, spots));
    }

    @Before
    public void setUpSnapshotFileRepo() {
        File dirToStore = new File(System.getProperty("java.io.tmpdir"));
        System.out.println("Saving all aggregates in " + dirToStore.getAbsolutePath());
        snapshotFileRepo = new SnapshotFileRepo(dirToStore, new JMomObjectMapper());
        commandHandler =  new ThingRepositoryHandler(thingRepository, snapshotFileRepo);
    }

    @Test
    public void save() throws IOException {
        ThingRepository actual = snapshotFileRepo.load(ThingRepository.class);

        assertThat(actual.toString()).isEqualTo(thingRepository.toString());
        assertThat(actual.getVersion()).isEqualTo(7);
    }

}