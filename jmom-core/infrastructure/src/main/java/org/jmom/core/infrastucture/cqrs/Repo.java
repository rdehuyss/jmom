package org.jmom.core.infrastucture.cqrs;


import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Throwables.propagate;

public interface Repo {

    public static enum FallbackMode {
        NULL {
            public <T extends AggregateRoot> T get(Class<T> aggregateClass) {
                return null;
            }
        },
        NEW_INSTANCE {
            public <T extends AggregateRoot> T get(Class<T> aggregateClass) {
                try {
                    Constructor<T> defaultConstructor = aggregateClass.getConstructor();
                    return defaultConstructor.newInstance();
                } catch (Exception e1) {
                    throw propagate(e1);
                }
            }
        };

        public abstract <T extends AggregateRoot> T get(Class<T> aggregateClass);
    }

    void save(AggregateRoot aggregateRoot) throws IOException;

    Map<String, AggregateRoot> listAggregateRoots();

    <T extends AggregateRoot> T load(Class<T> aggregateClass);

    <T extends AggregateRoot> T load(String aggregateClassName, FallbackMode fallbackMode);

    <T extends AggregateRoot> T load(Class<T> aggregateClass, FallbackMode fallbackMode);
}
