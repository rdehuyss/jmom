package org.jmom.core.model.eda;

import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.Thing;
import org.jmom.core.model.things.ThingTree;

public class SaveThingCommand extends Command {

    private final Path parentPath;
    private final Thing child;

    public SaveThingCommand(ThingTree parent, Thing child) {
        this(parent.getPath(), child);
    }

    public SaveThingCommand(Path parentPath, Thing child) {
        this.parentPath = parentPath;
        this.child = child;
    }

    public Path getParentPath() {
        return parentPath;
    }

    public Thing getChild() {
        return child;
    }
}
