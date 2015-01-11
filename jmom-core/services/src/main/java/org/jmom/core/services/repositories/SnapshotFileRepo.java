package org.jmom.core.services.repositories;

import com.google.common.collect.JMomFluentIterable;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.jmom.core.infrastucture.cqrs.AggregateRoot;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import static com.google.common.collect.JMomFluentIterable.from;

public class SnapshotFileRepo implements Repo {

    private final Map<String, AggregateRoot> cache;
    private final File dirToStore;
    private final JMomObjectMapper objectMapper;

    public SnapshotFileRepo(File dirToStore, JMomObjectMapper objectMapper) {
        this.cache = Maps.newHashMap();
        this.dirToStore = dirToStore;
        this.objectMapper = objectMapper;
        this.dirToStore.mkdirs();
    }

    //not really transactional but good enough for now
    @Override
    public void save(AggregateRoot aggregateRoot) throws IOException {
        //from(aggregate.getUncommittedChanges()).forEach(event -> eventBus.post(event));
        aggregateRoot.markChangesAsCommited();
        cache.put(aggregateRoot.getClass().getName(), aggregateRoot);
        objectMapper.writeValue(getAggregateFile(aggregateRoot.getClass().getName()), aggregateRoot);
    }

    @Override
    public Map<String, AggregateRoot> listAggregateRoots() {
        return cache;
    }

    @Override
    public <T extends AggregateRoot> T load(Class<T> aggregateClass) {
        return load(aggregateClass, FallbackMode.NEW_INSTANCE);
    }


    @Override
    public <T extends AggregateRoot> T load(String aggregateClassName, FallbackMode fallbackMode) {
        try {
            return load((Class<T>) Class.forName(aggregateClassName), fallbackMode);
        } catch (ClassNotFoundException shouldNotHappen) {
            throw new RuntimeException("Error deserializing class '" + aggregateClassName + "'", shouldNotHappen);
        }
    }

    @Override
    public <T extends AggregateRoot> T load(Class<T> aggregateClass, FallbackMode fallbackMode) {
        if (!cache.containsKey(aggregateClass.getName())) {
            try {
                cache.put(aggregateClass.getName(), objectMapper.readValue(getAggregateFile(aggregateClass.getName()), aggregateClass));
            } catch (FileNotFoundException e) {
                T t = fallbackMode.get(aggregateClass);
                cache.put(aggregateClass.getName(), t);
            } catch (IOException e) {
                throw new RuntimeException("Error deserializing class '" + aggregateClass.getSimpleName() + "'", e);
            }
        }
        return (T) cache.get(aggregateClass.getName());
    }


    private File getAggregateFile(String aggregateId) {
        return new File(dirToStore, aggregateId);
    }

}
