package org.jmom.apps.server;

import org.jmom.core.infrastucture.DIGraph;
import org.jmom.core.infrastucture.JMomInfrastructureModule;
import org.jmom.core.model.JMomModelModule;
import org.jmom.core.services.JMomServicesModule;
import org.jmom.interfaces.rfxcom.RFXComModule;

import java.io.File;

import static org.jmom.core.infrastucture.DIGraph.aDIGraph;

public class ServerModule {

    public static class Init {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating ServerModule.Init");
                diGraph = aDIGraph()
                        .basedOn(JMomInfrastructureModule.Init.diGraph(), JMomModelModule.Init.diGraph(), JMomServicesModule.Init.diGraph())
                        .register(new File(System.getProperty("java.io.tmpdir"), "jmom-repo"));
            }
            return diGraph;
        }

    }

    public static class Discovery {
        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating ServerModule.Discovery");
                diGraph = aDIGraph()
                        .basedOn(Init.diGraph(), JMomModelModule.Discovery.diGraph(), RFXComModule.Discovery.diGraph(), JMomServicesModule.Discovery.diGraph());
            }
            return diGraph;
        }
    }

    public static class Runtime {
        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating ServerModule.Runtime");
                diGraph = aDIGraph()
                        .basedOn(Init.diGraph(), JMomModelModule.Runtime.diGraph(), RFXComModule.Runtime.diGraph(), JMomServicesModule.Runtime.diGraph());
            }
            return diGraph;
        }

    }


//    @Provides
//    @Singleton
//    public ServiceManager provideServiceManager(Set<Service> services) {
//        return new ServiceManager(services);
//    }

}
