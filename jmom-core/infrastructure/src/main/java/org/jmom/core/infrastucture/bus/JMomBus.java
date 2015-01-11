package org.jmom.core.infrastucture.bus;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.jmom.core.infrastucture.eda.Message;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.google.common.base.Predicates.and;
import static com.google.common.collect.JMomFluentIterable.from;

public class JMomBus {

    private EventBus eventBus = new EventBus();
    private Set<JMomBusInterceptor> interceptors;

    public JMomBus() {
        this.interceptors = Sets.newHashSet();
    }

    public void post(Message object) {
        Optional<Message> element = from(object)
                .filter(and(interceptors))
                .first();
        if (element.isPresent()) {
            eventBus.post(element.get());
        }
    }

    public void register(Object object) {
        eventBus.register(object);
    }

    public void unregister(Object object) {
        eventBus.unregister(object);
    }

    public void registerInterceptor(JMomBusInterceptor interceptor) {
        interceptors.add(interceptor);
    }
    public void unregisterInterceptor(JMomBusInterceptor interceptor) {
        interceptors.remove(interceptor);
    }

    public void waitFor(Class<? extends Message> clazz, long timeoutInSeconds) {
        waitFor(clazz, timeoutInSeconds, TimeUnit.SECONDS);
    }

    public void waitFor(Class<? extends Message> clazz, long timeout, TimeUnit timeUnit) {
        WaitForChange waitForChange = new WaitForChange(eventBus, clazz, timeout, timeUnit);
        waitForChange.waitForIt();
    }

    private static class WaitForChange {

        private final EventBus eventBus;
        private final Class<? extends Message> clazz;
        private final long timeout;
        private final TimeUnit timeUnit;
        private CountDownLatch countDownLatch;

        WaitForChange(EventBus eventBus, Class<? extends Message> clazz, long timeout, TimeUnit timeUnit) {
            this.eventBus = eventBus;
            this.clazz = clazz;
            this.timeout = timeout;
            this.timeUnit = timeUnit;
        }

        @Subscribe
        public void onMessage(Message message) {
            if (clazz.isAssignableFrom(message.getClass())) {
                countDownLatch.countDown();
            }
        }

        void waitForIt() {
            countDownLatch = new CountDownLatch(1);
            eventBus.register(this);
            try {
                countDownLatch.await(timeout, timeUnit);
            } catch (InterruptedException shouldNotHappen) {
                shouldNotHappen.printStackTrace();
            } finally {
                eventBus.unregister(this);
            }
        }
    }
}
