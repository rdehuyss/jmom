package org.jmom.interfaces.rfxcom;

import com.google.common.util.concurrent.Service;
import dagger.Module;
import dagger.Provides;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.infrastucture.bus.JMomEventBus;

import javax.inject.Singleton;

@Module(
        library = true,
        complete = false
)
public class RFXComModule {

    @Singleton
    @Provides
    public RFXComInterfaceProvider provideRFXComInterface(JMomEventBus eventBus) {
        return new RFXComInterfaceProvider(eventBus);
    }

    @Provides(type = Provides.Type.SET)
    public Service rfxComInterfaceAsService(RFXComInterfaceProvider rfxComInterfaceProvider) {
        return rfxComInterfaceProvider;
    }

    @Provides(type = Provides.Type.SET)
    public JMomBusAware rfxComInterfaceAsJMomBusAware(RFXComInterfaceProvider rfxComInterfaceProvider) {
        return rfxComInterfaceProvider;
    }
}
