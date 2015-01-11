package org.jmom.core.model.things;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Splitter;

import java.util.Iterator;
import java.util.Objects;

public class Path {

    private static final String SEPARATOR = "/";
    private final StringBuilder value = new StringBuilder();

    private Path() {
    }

    public Path(String name) {
        if(!name.isEmpty()) {
            value.append(SEPARATOR).append(name);
        }
    }

    public Path(Path parentPath, String name) {
        this.value.append(parentPath.value).append(SEPARATOR).append(name);
    }

    public Iterator<String> elementIterator() {
        final Iterable<String> split = Splitter.on(SEPARATOR).omitEmptyStrings().split(value);
        return split.iterator();
    }

    public Path getParent() {
        return fromString(value.substring(0, value.lastIndexOf("/")));
    }

    public String getName() {
        return value.substring(value.lastIndexOf(SEPARATOR + 1));
    }

    public String toString() {
        return value.toString();
    }

    public static Path root() {
        return new Path();
    }

    public boolean isRoot() {
        return value.length() < 1;
    }

    @JsonCreator
    public static Path fromString(@JsonProperty("value")  String pathAsString) {
        Path path = new Path();
        path.value.append(pathAsString);
        return path;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Path other = (Path) obj;
        return Objects.equals(this.value.toString(), other.value.toString());
    }
}
