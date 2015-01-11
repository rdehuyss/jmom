package org.jmom.core.model;

import org.jmom.core.infrastucture.DIGraph;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.controlunit.ControlUnit;
import org.jmom.core.model.controlunit.ControlUnitRepository;
import org.jmom.core.model.eda.events.CentralControlUnitJMomBusInterceptor;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;
import org.jmom.core.model.interfacing.InterfaceProvider;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;

import static org.jmom.core.infrastucture.DIGraph.DIRequestBuilder.aDIRequest;
import static org.jmom.core.infrastucture.DIGraph.DIRuleBuilder.aDIRule;
import static org.jmom.core.infrastucture.DIGraph.aDIGraph;

public class JMomModelModule {

    public static class Init {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating ModelModule.Init");
                diGraph = aDIGraph()
                        .register(aDIRequest().dependsOn(Repo.class).create(diGraph -> diGraph.getBean(Repo.class).load(ControlUnitRepository.class)))
                        .register(aDIRequest().dependsOn(Repo.class).create(diGraph -> diGraph.getBean(Repo.class).load(ConfigurationRepository.class)).asPrototypeBean())
                        .register(aDIRequest()
                                .registerAs(ControlUnit.class)
                                .dependsOn(ControlUnitRepository.class)
                                .precondition(diGraph -> diGraph.getBean(ControlUnitRepository.class).isControlUnitConfigured())
                                .create(diGraph -> diGraph.getBean(ControlUnitRepository.class).getControlUnit()))
                        .register(aDIRequest()
                                .dependsOn(ControlUnit.class)
                                .precondition(diGraph -> diGraph.getBean(ControlUnit.class).isCentralControlUnit())
                                .create(diGraph -> new CentralControlUnitJMomBusInterceptor((CentralControlUnit) diGraph.getBean(ControlUnit.class))));
            }
            return diGraph;
        }
    }

    public static class Discovery {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating ModelModule.Discovery");
                diGraph = aDIGraph()
                        .withRule(aDIRule()
                                .objectAssignableFrom(InterfaceDiscoverer.class)
                                .onPostConstruct((obj, diGraph) -> diGraph.register(InterfaceDiscoverer.class, obj)))
                        ;
            }
            return diGraph;
        }
    }

    public static class Runtime {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                System.out.println("Creating ModelModule.Runtime");
                diGraph = aDIGraph()
                        .withRule(aDIRule()
                                .objectAssignableFrom(InterfaceProvider.class)
                                .onPostConstruct((obj, diGraph) -> diGraph.register(InterfaceProvider.class, obj)))
                        .register(aDIRequest()
                                .dependsOn(Repo.class)
                                .create(diGraph -> diGraph.getBean(Repo.class).load(StateRepository.class))
                                .asPrototypeBean())
                        .register(aDIRequest()
                                .dependsOn(Repo.class)
                                .create(diGraph -> diGraph.getBean(Repo.class).load(ThingRepository.class))
                                .asPrototypeBean());
            }
            return diGraph;
        }
    }

}
