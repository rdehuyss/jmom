package org.jmom.core.infrastucture.cqrs;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Throwables;

import java.util.List;

import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.JMomFluentIterable.from;
import static com.google.common.collect.Lists.newArrayList;


public abstract class AggregateRoot {

    @JsonIgnore
    private final List<DomainEvent> uncommittedChanges = newArrayList();

    private long version;

    public AggregateRoot() {
    }

    public long getVersion() {
        return version;
    }

    public void markChangesAsCommited() {
        version = version + uncommittedChanges.size();
        uncommittedChanges.clear();
    }

    public List<DomainEvent> getUncommittedChanges() {
        return uncommittedChanges;
    }

    public <T> T loadFromHistory(List<DomainEvent> history) {
        from(history).forEachItem(event -> apply(event, false));
        return (T) this;
    }

    public void apply(DomainEvent domainEvent) {
        apply(domainEvent, true);
    }

    public void applyInMemoryOnly(DomainEvent domainEvent) {
        try {
            domainEvent.process(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void apply(DomainEvent domainEvent, boolean isNew) {
        try {
            domainEvent.process(this);
            if (isNew) {
                uncommittedChanges.add(domainEvent);
            } else {
                version++;
            }
        } catch (Exception e) {
            throw propagate(e);
        }
    }

}
