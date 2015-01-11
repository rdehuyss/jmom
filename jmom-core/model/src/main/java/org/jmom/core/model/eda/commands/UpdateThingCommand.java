package org.jmom.core.model.eda.commands;

import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.Thing;

public class UpdateThingCommand extends Command {

    private final Path originalPath;
    private final Path parentPath;
    private final Thing child;

    public UpdateThingCommand(Path originalPath, Path parentPath, Thing child) {
        this.originalPath = originalPath;
        this.parentPath = parentPath;
        this.child = child;
    }

    public Path getOriginalPath() {
        return originalPath;
    }

    public Path getParentPath() {
        return parentPath;
    }

    public Thing getChild() {
        return child;
    }
}
