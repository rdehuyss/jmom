package org.jmom.core.model.things;

import com.google.common.base.Optional;
import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.DomainEvent;
import org.jmom.core.model.eda.SaveThingCommand;
import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.core.model.things.devices.Device;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.typelibrary.StateChange;

import static com.google.common.base.Predicates.and;
import static com.google.common.base.Predicates.instanceOf;
import static org.jmom.core.model.things.devices.Device.hasDeviceIdentifier;

public class ThingRepository extends AggregateRoot {

    private ThingTree thingTree;

    public ThingRepository() {
        thingTree = new ThingTree("");
    }

    public <S extends Thing> Optional<S> getByPath(Path path) {
        return thingTree.getByPath(path);
    }

    private void handle(UpdateOrSaveThingDomainEvent command) {
        Optional<Thing> thing = getByPath(new Path(command.parentPath, command.child.getName()));
        if (thing.isPresent()) {
            updateThing(thing.get(), command.child);
        } else {
            Optional<ThingTree> parent = getByPath(command.parentPath);
            if (!parent.isPresent()) {
                throw parentMissing(command);
            }
            saveThing(parent.get(), command.child);
        }
    }

    private void handle(UpdateStateChangeDomainEvent command) {
        thingTree.descendantsOrSelf().filter(
                and(
                        instanceOf(Device.class),
                        hasDeviceIdentifier(command.deviceIdentifier)
                ))
                .forEachItem(device -> ((Device) device).setState(command.stateChange));
    }

    //TODO: can we do this better? Benefit is no confusion of how to use it (one cannot interact via the ThingTree itself, one must use an event)
    private void saveThing(ThingTree thingTree, Thing child) {
        child.parent = thingTree;
        thingTree.children.add(child);
    }

    private void updateThing(Thing thingToUpdate, Thing newThing) {
        thingToUpdate.updateWith(newThing);
    }

    private IllegalArgumentException parentMissing(UpdateOrSaveThingDomainEvent command) {
        return new IllegalArgumentException(String.format("Can not save %s '%s' as parent with path %s does not exist",
                command.child.getClass().getSimpleName(),
                command.child.getName(),
                command.parentPath));
    }

    @Override
    public String toString() {
        return thingTree.toString();
    }

    public static class UpdateOrSaveThingDomainEvent implements DomainEvent<ThingRepository> {
        private final Path parentPath;
        private final Thing child;

        public UpdateOrSaveThingDomainEvent(SaveThingCommand command) {
            this.parentPath = command.getParentPath();
            this.child = command.getChild();
        }

        @Override
        public void process(ThingRepository repository) {
            repository.handle(this);
        }

    }

    public static class UpdateStateChangeDomainEvent implements DomainEvent<ThingRepository> {

        private final DeviceIdentifier deviceIdentifier;
        private final StateChange stateChange;

        public UpdateStateChangeDomainEvent(StateChangedEvent stateChangedEvent) {
            deviceIdentifier = stateChangedEvent.getDeviceIdentifier();
            stateChange = stateChangedEvent.getNewState();
        }


        @Override
        public void process(ThingRepository repository) {
            repository.handle(this);
        }
    }

}
