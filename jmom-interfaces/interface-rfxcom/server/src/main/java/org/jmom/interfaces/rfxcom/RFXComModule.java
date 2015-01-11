package org.jmom.interfaces.rfxcom;

import org.jmom.core.infrastucture.DIGraph;
import org.jmom.core.infrastucture.bus.JMomBus;

import static org.jmom.core.infrastucture.DIGraph.DIRequestBuilder.aDIRequest;
import static org.jmom.core.infrastucture.DIGraph.aDIGraph;

public class RFXComModule {


    public static class Discovery {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                diGraph = aDIGraph()
                        .register(aDIRequest().dependsOn(JMomBus.class).create(diGraph -> new RFXComSerialInterfaceDiscoverer(diGraph.getBean(JMomBus.class))));
            }
            return diGraph;
        }

    }

    public static class Runtime {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                diGraph = aDIGraph()
                        .register(aDIRequest()
                                .dependsOn(JMomBus.class)
                                .create(diGraph -> new RFXComInterfaceProvider(diGraph.getBean(JMomBus.class))));
            }
            return diGraph;
        }
    }
}
