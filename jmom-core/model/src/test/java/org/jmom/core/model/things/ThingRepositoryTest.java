package org.jmom.core.model.things;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.eda.commands.DeleteThingCommand;
import org.jmom.core.model.eda.commands.SaveThingCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.eda.commands.UpdateThingCommand;
import org.jmom.core.model.things.ThingRepository.DeleteThingDomainEvent;
import org.jmom.core.model.things.ThingRepository.SaveThingDomainEvent;
import org.jmom.core.model.things.ThingRepository.UpdateStateChangeDomainEvent;
import org.jmom.core.model.things.ThingRepository.UpdateThingDomainEvent;
import org.jmom.core.model.things.devices.Device;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.Light;
import org.jmom.core.model.things.devices.typelibrary.OnOffChange;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static com.google.common.base.Predicates.instanceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jmom.core.model.things.Path.fromString;
import static org.jmom.core.model.things.Thing.byName;
import static org.jmom.core.model.things.devices.Device.hasDeviceIdentifier;

public class ThingRepositoryTest {

    private ThingRepository thingRepository = new ThingRepository();

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
                .addIdentifier(new DeviceIdentifier("id-1"))
                .setState(OnOffChange.ON);

        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(Path.root(), house)));
        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(house, gelijkvloers)));
        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(house, eersteVerdieping)));
        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(gelijkvloers, living)));
        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(gelijkvloers, keuken)));
        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(gelijkvloers, eetkamer)));
        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(living, spots)));
    }

    @Test
    public void getByPath_Root() {
        Optional<ThingTree> root = thingRepository.getByPath(Path.root());
        ThingTree actual = root.get();
        List<? extends Thing> children = actual.getChildren();
        assertThat(children).hasSize(1);
        assertThat(children.get(0).getName()).isEqualTo("House Pastorijstraat");
    }

    @Test
    public void getByPath_UnderLocation() {
        Optional<ThingTree> thingTree = thingRepository.getByPath(fromString("/House Pastorijstraat"));
        assertThat(thingTree.get()).isEqualTo(house);
    }


    @Test(expected = IllegalArgumentException.class)
    public void saveThing_ThingAlreadyExists() {
        thingRepository.apply(new SaveThingDomainEvent(new SaveThingCommand(living, spots)));
    }

    @Test
    public void updateThing_ThingAlreadyExists() {
        Light nieuweSpots = new Light("Nieuwe Spots")
                .addIdentifier(new DeviceIdentifier("id-1"))
                .setState(OnOffChange.ON);
        thingRepository.apply(new UpdateThingDomainEvent(new UpdateThingCommand(spots.getPath(), keuken.getPath(), nieuweSpots)));

        assertThat(nieuweSpots.getParent()).isNull();
        assertThat(spots.getParent()).isEqualTo(keuken);
        assertThat(living.getChildren()).isEmpty();
        assertThat(spots.getPath().toString()).isEqualTo("/House Pastorijstraat/Gelijkvloers/Keuken/Nieuwe Spots");
        ImmutableList<Device> devices = this.house.descendantsOrSelf().filter(Device.class).filter(hasDeviceIdentifier(new DeviceIdentifier("id-1"))).toList();
        assertThat(devices)
                .hasSize(1)
                .containsOnly(spots);
    }

    @Test
    public void deleteUnderLocation() {
        thingRepository.apply(new DeleteThingDomainEvent(new DeleteThingCommand(living.getPath())));
        ImmutableList<Thing> actualLocations = this.house.descendantsOrSelf().filter(instanceOf(Location.class)).toList();
        assertThat(actualLocations).hasSize(5);
        Optional<Device> device = this.house.descendantsOrSelf().filter(Device.class).firstMatch(hasDeviceIdentifier(new DeviceIdentifier("id-1")));
        assertThat(device.isPresent()).isFalse();
    }

    @Test
    public void testTreeNode() {
        assertThat(spots.getLevel()).isEqualTo(4);
        assertThat(spots.getPath().toString()).isEqualTo("/House Pastorijstraat/Gelijkvloers/Living/Spots");

        assertThat(thingRepository.getByPath(fromString("/House Pastorijstraat/Gelijkvloers/Living/Spots")).get()).isEqualTo(spots);
    }

    @Test
    public void filter() throws IOException {
        Thing actualHouse = this.house.descendantsOrSelf().firstMatch(byName("House Pastorijstraat")).get();
        assertThat(actualHouse).isEqualTo(house);
        Thing actualSpots = this.house.descendantsOrSelf().firstMatch(byName("Spots")).get();
        assertThat(actualSpots).isEqualTo(spots);
        ImmutableList<Thing> actualLocations = this.house.descendantsOrSelf().filter(instanceOf(Location.class)).toList();
        assertThat(actualLocations).hasSize(6);
        Thing device = this.house.descendantsOrSelf().filter(Device.class).firstMatch(hasDeviceIdentifier(new DeviceIdentifier("id-1"))).get();
        assertThat(device).isEqualTo(spots);
    }




    @Test
    public void changeState() {
        assertThat(spots.getState()).isEqualTo(OnOffChange.ON);
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier("id-1");
        StateChangedEvent stateChangedEvent = new StateChangedEvent(deviceIdentifier, OnOffChange.OFF);
        thingRepository.apply(new UpdateStateChangeDomainEvent(stateChangedEvent));
        assertThat(spots.getState()).isEqualTo(OnOffChange.OFF);
    }

    @Test
    public void changeStateInMemoryDoesNotChangeVersion() {
        assertThat(thingRepository.getUncommittedChanges()).hasSize(7);
        DeviceIdentifier deviceIdentifier = new DeviceIdentifier("id-1");
        StateChangedEvent stateChangedEvent = new StateChangedEvent(deviceIdentifier, OnOffChange.OFF);
        thingRepository.applyInMemoryOnly(new UpdateStateChangeDomainEvent(stateChangedEvent));
        assertThat(thingRepository.getUncommittedChanges()).hasSize(7);
    }

    @Test
    public void serializationTest() throws IOException {
        ObjectMapper mapper = new JMomObjectMapper();

        String treeAsString = mapper.writeValueAsString(thingRepository);
        System.out.println(treeAsString);

        ThingRepository actual = mapper.readValue(treeAsString, ThingRepository.class);

        System.out.println(actual);
        assertThat(actual.toString()).isEqualTo(thingRepository.toString());
    }
}