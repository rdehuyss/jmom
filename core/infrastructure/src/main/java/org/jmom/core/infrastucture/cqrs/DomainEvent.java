package org.jmom.core.infrastucture.cqrs;

public interface DomainEvent<T extends AggregateRoot> {

    void process(T aggregateRoot);

}
