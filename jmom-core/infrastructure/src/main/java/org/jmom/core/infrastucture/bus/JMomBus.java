package org.jmom.core.infrastucture.bus;

import com.google.common.base.Optional;
import com.google.common.eventbus.EventBus;

import java.util.Set;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.JMomFluentIterable.from;
import static com.google.common.collect.Sets.newHashSet;

public class JMomBus<T extends Object> {

    private EventBus eventBus = new EventBus();
    private Set<JMomBusFilter<T>> filters = newHashSet();

    public void post(T object) {
        Optional<T> element = from(object)
                .filter(and(filters))
                .first();
        if(element.isPresent()) {
            eventBus.post(element.get());
        }
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void unregister(Object object) {
        eventBus.unregister(object);
    }

    public void addFilter(JMomBusFilter<T> filter) {
        filters.add(filter);
    }
}
