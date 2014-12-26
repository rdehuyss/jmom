package org.jmom.core.model.things;

import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.Assertions;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.things.devices.Light;
import org.jmom.core.model.things.devices.typelibrary.OnOffChange;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.google.common.base.Predicates.instanceOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.jmom.core.model.things.Path.fromString;
import static org.jmom.core.model.things.Thing.byName;
import static org.junit.Assert.*;

public class ThingTreeTest {

    private Location house;
    private Location gelijkvloers;
    private Location eersteVerdieping;
    private Location living;
    private Location keuken;
    private Location eetkamer;
    private Light spots;

    @Before
    public void setUpEnv() {
        house = new Location("House Pastorijstraat");
        gelijkvloers = new Location("Gelijkvloers");
        eersteVerdieping = new Location("Eerste verdieping");
        living = new Location("Living");
        keuken = new Location("Keuken");
        eetkamer = new Location("Eetkamer");
        spots = new Light("Spots")
                .withAttribute("identifier", "id-1")
                .setState(OnOffChange.ON);

        setParentChildRelationship(house, gelijkvloers);
        setParentChildRelationship(house, eersteVerdieping);
        setParentChildRelationship(gelijkvloers, living);
        setParentChildRelationship(gelijkvloers, keuken);
        setParentChildRelationship(gelijkvloers, eersteVerdieping);
        setParentChildRelationship(living, spots);
    }

    @Test
    public void testTreeNode() {
        assertThat(spots.getLevel()).isEqualTo(3);
        assertThat(spots.getPath()).isEqualTo(fromString("/House Pastorijstraat/Gelijkvloers/Living/Spots"));

        assertThat(house.getByPath(fromString("/Gelijkvloers/Living/Spots")).get()).isEqualTo(spots);
    }

    @Test
    public void toJackson() throws IOException {
        JMomObjectMapper mapper = new JMomObjectMapper();

        String treeAsString = mapper.writeValueAsString(house);
        System.out.println(treeAsString);

        Location thingTree = mapper.readValue(treeAsString, Location.class);

        System.out.println(thingTree);
    }

    @Test
    public void filter() throws IOException {
        Thing actualHouse = this.house.descendantsOrSelf().firstMatch(byName("House Pastorijstraat")).get();
        assertThat(actualHouse).isEqualTo(house);
        Thing actualSpots = this.house.descendantsOrSelf().firstMatch(byName("Spots")).get();
        assertThat(actualSpots).isEqualTo(spots);
        ImmutableList<Thing> actualLocations = this.house.descendantsOrSelf().filter(instanceOf(Location.class)).toList();
        assertThat(actualLocations).hasSize(6);
    }

    private void setParentChildRelationship(ThingTree tree, Thing thing) {
        tree.children.add(thing);
        thing.parent = tree;
    }

}