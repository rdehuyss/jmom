package org.jmom.core.services.repositories;

import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;

import static com.google.common.base.Throwables.propagate;

public class SnapshotFileRepo implements Repo {

    private final File eventStore;
    private final JMomObjectMapper objectMapper;

    public SnapshotFileRepo(File dirToStore, JMomObjectMapper objectMapper) {
        this.eventStore = dirToStore;
        this.objectMapper = objectMapper;
    }

    //not really transactional but good enough for now
    @Override
    public void save(AggregateRoot aggregate) throws IOException {
        //from(aggregate.getUncommittedChanges()).forEach(event -> eventBus.post(event));
        aggregate.markChangesAsCommited();
        objectMapper.writeValue(getAggregateFile(aggregate.getClass().getSimpleName()), aggregate);
    }

    @Override
    public <T extends AggregateRoot> T load(Class<T> aggregateClass) {
        try {
            return objectMapper.readValue(getAggregateFile(aggregateClass.getSimpleName()), aggregateClass);
        } catch (FileNotFoundException e) {
            try {
                Constructor<T> defaultConstructor = aggregateClass.getConstructor();
                return defaultConstructor.newInstance();
            } catch (Exception e1) {
                throw propagate(e1);
            }
        } catch (IOException e) {
            throw propagate(e);
        }
    }


    private File getAggregateFile(String aggregateId) {
        return new File(eventStore, aggregateId);
    }

}
