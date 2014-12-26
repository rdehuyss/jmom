package org.jmom.core.services;

import com.google.common.util.concurrent.Service;
import dagger.Module;
import dagger.Provides;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.infrastucture.bus.JMomCommandBus;
import org.jmom.core.infrastucture.bus.JMomEventBus;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.infrastucture.qualifiers.DeviceName;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;
import org.jmom.core.services.handlers.StateRepositoryHandler;
import org.jmom.core.services.handlers.ThingRepositoryHandler;
import org.jmom.core.services.remoting.client.MQTTClient;
import org.jmom.core.services.repositories.SnapshotFileRepo;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;

@Module(
        library = true,
        complete = false
)
public class JMomServicesModule {

    @Provides
    @Singleton
    public Repo providesSnapshotRepo(File provideDirToStoreAllAggregates, JMomObjectMapper objectMapper) {
        return new SnapshotFileRepo(provideDirToStoreAllAggregates, objectMapper);
    }

    @Provides(type = Provides.Type.SET)
    @Singleton
    public JMomBusAware provideThingRepositoryHandler(ThingRepository thingRepository, Repo repo) {
        ThingRepositoryHandler handler = new ThingRepositoryHandler(thingRepository, repo);
        return handler;
    }

    @Provides(type = Provides.Type.SET)
    @Singleton
    public JMomBusAware provideStateRepositoryHandler(StateRepository stateRepository, Repo repo) {
        StateRepositoryHandler handler = new StateRepositoryHandler(stateRepository, repo);
        return handler;
    }

    @Provides(type = Provides.Type.SET)
    @Singleton
    public Service provideMQTTClient(@DeviceName String deviceName, JMomCommandBus commandBus, JMomEventBus eventBus, JMomObjectMapper objectMapper) {
        MQTTClient client = new MQTTClient("ronald.dehuysser@gmail.com", deviceName, commandBus, eventBus, objectMapper);
        return client;
    }

}
