package org.jmom.core.model.things;

import com.google.common.base.Optional;
import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.DomainEvent;
import org.jmom.core.model.eda.commands.DeleteThingCommand;
import org.jmom.core.model.eda.commands.SaveThingCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.eda.commands.UpdateThingCommand;
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

    private void handle(SaveThingDomainEvent domainEvent) {
        Optional<Thing> thing = getByPath(new Path(domainEvent.parentPath, domainEvent.child.getName()));
        if (thing.isPresent()) {
            throw new IllegalArgumentException("A thing with type " + thing.get().getClass().getSimpleName() + " already exists for path '" + thing.get().getPath() + "'");
        } else {
            Optional<ThingTree> parent = getByPath(domainEvent.parentPath);
            if (!parent.isPresent()) {
                throw parentMissing(domainEvent.parentPath, domainEvent.child);
            }
            saveThing(parent.get(), domainEvent.child);
        }
    }

    private void handle(UpdateThingDomainEvent domainEvent) {
        Optional<Thing> thing = getByPath(domainEvent.originalPath);
        if (!thing.isPresent()) {
            throw new IllegalArgumentException("Thing to update with type " + domainEvent.child.getClass().getSimpleName() + " and path '" + thing.get().getPath() + "' could not be found");
        } else {
            Thing child = thing.get();
            deleteThing(child);
            Optional<ThingTree> parent = getByPath(domainEvent.parentPath);
            if (!parent.isPresent()) {
                throw parentMissing(domainEvent.parentPath, domainEvent.child);
            }
            child.updateWith(domainEvent.child);
            saveThing(parent.get(), child);
        }
    }

    private void handle(DeleteThingDomainEvent domainEvent) {
        Optional<Thing> thing = getByPath(domainEvent.path);
        if (thing.isPresent()) {
            deleteThing(thing.get());
        }
    }

    private void handle(UpdateStateChangeDomainEvent domainEvent) {
        thingTree.descendantsOrSelf().filter(
                and(
                        instanceOf(Device.class),
                        hasDeviceIdentifier(domainEvent.deviceIdentifier)
                ))
                .forEachItem(device -> ((Device) device).setState(domainEvent.stateChange));
    }

    private void saveThing(ThingTree thingTree, Thing child) {
        child.parent = thingTree;
        thingTree.children.add(child);
    }

    private void deleteThing(Thing child) {
        ThingTree parent = child.parent;
        child.parent = null;
        parent.children.remove(child);
    }

    private void updateThing(Thing thingToUpdate, Thing newThing) {
        thingToUpdate.updateWith(newThing);
    }

    private IllegalArgumentException parentMissing(Path parentPath, Thing child) {
        return new IllegalArgumentException(String.format("Can not save %s '%s' as parent with path %s does not exist",
                child.getClass().getSimpleName(),
                child.getName(),
                parentPath));
    }

    @Override
    public String toString() {
        return thingTree.toString();
    }

    public static class SaveThingDomainEvent implements DomainEvent<ThingRepository> {
        private final Path parentPath;
        private final Thing child;

        public SaveThingDomainEvent(SaveThingCommand command) {
            this.parentPath = command.getParentPath();
            this.child = command.getChild();
        }

        @Override
        public void process(ThingRepository repository) {
            repository.handle(this);
        }
    }

    public static class UpdateThingDomainEvent implements DomainEvent<ThingRepository> {
        private final Path originalPath;
        private final Path parentPath;
        private final Thing child;

        public UpdateThingDomainEvent(UpdateThingCommand command) {
            this.originalPath = command.getOriginalPath();
            this.parentPath = command.getParentPath();
            this.child = command.getChild();
        }

        @Override
        public void process(ThingRepository repository) {
            repository.handle(this);
        }
    }

    public static class DeleteThingDomainEvent implements DomainEvent<ThingRepository> {
        private final Path path;

        public DeleteThingDomainEvent(DeleteThingCommand command) {
            this.path = command.getPath();
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
