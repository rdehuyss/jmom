package org.jmom.core.infrastucture;

import com.google.common.collect.Sets;
import dagger.Module;
import dagger.Provides;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.infrastucture.bus.JMomBusRegistrar;
import org.jmom.core.infrastucture.bus.JMomCommandBus;
import org.jmom.core.infrastucture.bus.JMomEventBus;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;

import javax.inject.Singleton;
import java.util.Set;

@Module(
        library = true,
        complete = false
)
public class JMomInfrastructureModule {

    @Provides
    @Singleton
    public JMomObjectMapper provideObjectMapper() {
        return new JMomObjectMapper();
    }

    @Provides
    @Singleton
    public JMomCommandBus commandBus() {
        return new JMomCommandBus();
    }

    @Provides
    @Singleton
    public JMomEventBus eventBus() {
        return new JMomEventBus();
    }

    @Provides(type = Provides.Type.SET_VALUES)
    public Set<JMomBusAware> provideOverriddenJMomBusAware() {
        return Sets.newHashSet();
    }

    @Provides
    @Singleton
    public JMomBusRegistrar provideJMomBusRegistrar(JMomCommandBus commandBus, JMomEventBus eventBus, Set<JMomBusAware> handlers) {
        return new JMomBusRegistrar(commandBus, eventBus, handlers);
    }
}
