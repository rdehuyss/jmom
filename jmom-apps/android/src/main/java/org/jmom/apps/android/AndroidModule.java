package org.jmom.apps.android;

import android.content.Context;
import org.jmom.core.infrastucture.DIGraph;
import org.jmom.core.infrastucture.JMomInfrastructureModule;
import org.jmom.core.model.JMomModelModule;
import org.jmom.core.services.JMomServicesModule;

import java.io.File;

import static org.jmom.core.infrastucture.DIGraph.aDIGraph;

public class AndroidModule {

    public static class Init {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating AndroidModule.Init");
                diGraph = aDIGraph()
                        .basedOn(JMomInfrastructureModule.Init.diGraph(), JMomModelModule.Init.diGraph(), JMomServicesModule.Init.diGraph())
                        .register(new File(System.getProperty("java.io.tmpdir"), "jmom-repo"));
            }
            return diGraph;
        }

    }

    public static class Runtime {
        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating AndroidModule.Runtime");
                diGraph = aDIGraph()
                        .basedOn(Init.diGraph(), JMomModelModule.Runtime.diGraph(), JMomServicesModule.Runtime.diGraph());
            }
            return diGraph;
        }

    }


    private final JMomApplication application;

    public AndroidModule(JMomApplication application) {
        this.application = application;
    }

    /**
     * Allow the application context to be injected but require that it be annotated with
     * {@link ForApplication @Annotation} to explicitly differentiate it from an activity context.
     */
//    @Provides
    //  @Singleton
    //@ForApplication
    Context provideApplicationContext() {
        return application;
    }

}

