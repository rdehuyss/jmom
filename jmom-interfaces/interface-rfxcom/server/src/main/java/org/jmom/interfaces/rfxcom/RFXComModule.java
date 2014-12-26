package org.jmom.interfaces.rfxcom;

import com.google.common.util.concurrent.Service;
import dagger.Module;
import dagger.Provides;
import org.jmom.core.infrastucture.bus.JMomEventBus;

import javax.inject.Singleton;

@Module(
        library = true,
        complete = false
)
public class RFXComModule {

    @Provides(type = Provides.Type.SET)
    @Singleton
    public Service provideRFXComInterface(JMomEventBus eventBus) {
        return new RFXComInterfaceProvider(eventBus);
    }
}
