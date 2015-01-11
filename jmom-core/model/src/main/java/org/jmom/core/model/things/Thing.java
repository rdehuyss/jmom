package org.jmom.core.model.things;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.google.common.base.Strings.repeat;

@JsonTypeInfo(use = JsonTypeInfo.Id.MINIMAL_CLASS, include = JsonTypeInfo.As.PROPERTY, property = "@class")
public abstract class Thing<T extends Thing> {

    @JsonBackReference
    ThingTree<?> parent;
    private String name;
    private String description;
    private Map<String, Object> attributes;

    protected Thing() {
    }

    public Thing(String name) {
        this(name, null);
    }

    public Thing(String name, String description) {
        this.name = name;
        this.description = description;
        this.attributes = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return true;
    }

    public int getLevel() {
        if (this.isRoot()) {
            return 0;
        } else {
            return parent.getLevel() + 1;
        }
    }

    public ThingTree<?> getParent() {
        return parent;
    }

    public Path getPath() {
        if (this.isRoot()) {
            return new Path(getName());
        } else {
            return new Path(parent.getPath(), getName());
        }
    }

    public T withAttribute(String name, Object attribute) {
        this.attributes.put(name, attribute);
        return (T) this;
    }

    public <T> T getAttribute(String name) {
        return (T) attributes.get(name);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public Object removeAttribute(String name) {
        return attributes.remove(name);
    }

    protected <S extends Thing> Optional<S> getByPath(final Iterator<String> pathIterator) {
        if (name.equals(pathIterator.next())) {
            return Optional.of((S) this);
        }
        return Optional.absent();
    }

    protected void updateWith(Thing otherThing) {
        this.name = otherThing.name;
        this.description = otherThing.description;
        this.attributes.putAll(otherThing.attributes);
    }

    @Override
    public String toString() {
        String str = "";
        str += indent(this.getClass().getSimpleName() + ": " + this.getName());
        return str;
    }

    protected String indent(String toAppend) {
        return "\n" + repeat(" ", getLevel() * 4) + toAppend;
    }


    public static Predicate<Thing> byName(final String name) {
        return input -> name.equals(input.getName());
    }

}
