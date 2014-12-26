package org.jmom.apps.android;

import com.google.common.util.concurrent.Service;
import com.google.common.util.concurrent.ServiceManager;
import dagger.Module;
import dagger.Provides;
import org.jmom.core.infrastucture.JMomInfrastructureModule;
import org.jmom.core.infrastucture.bus.JMomBusRegistrar;
import org.jmom.core.infrastucture.qualifiers.DeviceName;
import org.jmom.core.model.JMomModelModule;
import org.jmom.core.services.JMomServicesModule;

import javax.inject.Singleton;
import java.io.File;
import java.util.Set;

@Module(
        includes = {JMomInfrastructureModule.class, JMomModelModule.class, JMomServicesModule.class},
        injects = {TestClass.class, JMomBusRegistrar.class},
        library = true
)
public class AndroidModule {

    @Provides
    @DeviceName
    public String deviceName() {
        return "GSM Ronald";
    }


    @Provides
    @Singleton
    public File provideDirToStoreAllAggregates() {
        return new File(System.getProperty("java.io.tmpdir"));
    }

    @Provides
    @Singleton
    public ServiceManager provideServiceManager(Set<Service> services) {
        return new ServiceManager(services);
    }

}

