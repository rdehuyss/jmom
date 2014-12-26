package org.jmom.core.infrastucture.cqrs;


import java.io.IOException;

public interface Repo {

    void save(AggregateRoot aggregateRoot) throws IOException;

    <T extends AggregateRoot> T load(Class<T> aggregateClass);
}
