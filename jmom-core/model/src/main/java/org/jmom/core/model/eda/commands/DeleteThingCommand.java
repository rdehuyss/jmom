package org.jmom.core.model.eda.commands;

import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.model.things.Path;

public class DeleteThingCommand extends Command {

    private final Path path;

    public DeleteThingCommand(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }
}
