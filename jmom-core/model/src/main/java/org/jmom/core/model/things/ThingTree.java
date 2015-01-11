package org.jmom.core.model.things;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.JMomFluentIterable;
import com.google.common.collect.TreeTraverser;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.collect.JMomFluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;

public class ThingTree<T extends ThingTree<T>> extends Thing<T> {

    @JsonManagedReference
    List<? extends Thing> children;

    protected ThingTree() {
    }

    protected ThingTree(String name) {
        this(name, null);
    }

    protected ThingTree(String name, String description) {
        super(name, description);
        this.children = new LinkedList<Thing>();
    }

    public boolean isLeaf() {
        return children.size() == 0;
    }

    public List<? extends Thing> getChildren() {
        return ImmutableList.copyOf(children);
    }

    public <S extends Thing> Optional<S> getByPath(Path path) {
        Iterator<String> iterator = path.elementIterator();
        if (!iterator.hasNext()) {
            return Optional.of((S) this);
        }
        return getByPath(iterator);
    }

    @Override
    protected <S extends Thing> Optional<S> getByPath(final Iterator<String> pathElementIterator) {
        String childName = pathElementIterator.next();
        Optional<S> optionalThing = (Optional<S>) from(children).firstMatch(byName(childName));
        if (pathElementIterator.hasNext()) {
            if (optionalThing.isPresent()) {
                return optionalThing.get().getByPath(pathElementIterator);
            }
            return Optional.absent();
        }
        return optionalThing;
    }

    public <S extends Thing> JMomFluentIterable<S> descendantsOrSelf() {
        return from((Iterable<S>) traverser().breadthFirstTraversal(this));
    }

    @Override
    public String toString() {
        String str = "";
        str += super.toString();
        if (!children.isEmpty()) {
            str += indent("  Children:");
            for (Thing child : children) {
                str += child.toString();
            }
        }
        return str;
    }

    protected void update(ThingTree otherThing) {
        super.updateWith(otherThing);
        children.addAll(otherThing.children);
    }

    private <S extends Thing> TreeTraverser<S> traverser() {
        return new TreeTraverser<S>() {
            @Override
            public Iterable<S> children(Thing root) {
                if (root instanceof ThingTree) {
                    return ((ThingTree) root).children;
                }
                return newArrayList();
            }
        };
    }
}
