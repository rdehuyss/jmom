package org.jmom.core.model;

import dagger.Module;
import dagger.Provides;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;

import javax.inject.Singleton;

@Module(
        library = true,
        complete = false
)
public class JMomModelModule {

    @Provides
    @Singleton
    public StateRepository provideStateRepository(Repo snapshotRepo) {
        return snapshotRepo.load(StateRepository.class);
    }

    @Provides
    @Singleton
    public ThingRepository provideThingRepository(Repo snapshotRepo) {
        return snapshotRepo.load(ThingRepository.class);
    }
}
