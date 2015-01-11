package org.jmom.core.infrastucture;

import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.bus.JMomBusAware;
import org.jmom.core.infrastucture.bus.JMomBusInterceptor;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;

import static org.jmom.core.infrastucture.DIGraph.DIRuleBuilder.aDIRule;
import static org.jmom.core.infrastucture.DIGraph.aDIGraph;

public class JMomInfrastructureModule {

    public static class Init {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if(diGraph == null) {
                diGraph = aDIGraph()
                        .withRule(aDIRule().dependsOn(JMomBus.class).objectAssignableFrom(JMomBusAware.class).onPostConstruct((obj, diGraph) -> diGraph.getBean(JMomBus.class).register(obj)))
                        .withRule(aDIRule().dependsOn(JMomBus.class).objectAssignableFrom(JMomBusInterceptor.class).onPostConstruct((obj, diGraph) -> diGraph.getBean(JMomBus.class).registerInterceptor((JMomBusInterceptor) obj)))
                        .withDestructionRule(aDIRule().dependsOn(JMomBus.class).objectAssignableFrom(JMomBusAware.class).onPostConstruct((obj, diGraph) -> diGraph.getBean(JMomBus.class).unregister(obj)))
                        .withDestructionRule(aDIRule().dependsOn(JMomBus.class).objectAssignableFrom(JMomBusInterceptor.class).onPostConstruct((obj, diGraph) -> diGraph.getBean(JMomBus.class).unregisterInterceptor((JMomBusInterceptor) obj)))
                        .register(new JMomObjectMapper())
                        .register(new JMomBus());
            }
            return diGraph;
        }

    }
}
