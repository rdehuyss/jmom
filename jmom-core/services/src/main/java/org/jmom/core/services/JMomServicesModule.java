package org.jmom.core.services;

import org.jmom.core.infrastucture.DIGraph;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.cqrs.Repo;
import org.jmom.core.infrastucture.serialization.JMomObjectMapper;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.controlunit.ControlUnit;
import org.jmom.core.model.controlunit.ControlUnitRepository;
import org.jmom.core.model.interfacing.InterfaceDiscoverer;
import org.jmom.core.model.interfacing.InterfaceProvider;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;
import org.jmom.core.services.handlers.ConfigurationRepositoryHandler;
import org.jmom.core.services.handlers.ControlUnitRepositoryHandler;
import org.jmom.core.services.handlers.StateRepositoryHandler;
import org.jmom.core.services.handlers.ThingRepositoryHandler;
import org.jmom.core.services.interfacing.DuplicateInterfaceStateChangedEventFilter;
import org.jmom.core.services.interfacing.InterfaceDiscoveryManager;
import org.jmom.core.services.interfacing.InterfaceProviderManager;
import org.jmom.core.services.remoting.client.JMomBusOverMQTTClient;
import org.jmom.core.services.remoting.client.MQTTClientService;
import org.jmom.core.services.remoting.client.SyncedRepoOverMQTTClient;
import org.jmom.core.services.repositories.SnapshotFileRepo;

import java.io.File;

import static org.jmom.core.infrastucture.DIGraph.DIRequestBuilder.aDIRequest;
import static org.jmom.core.infrastucture.DIGraph.aDIGraph;

public class JMomServicesModule {

    public static class Init {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                diGraph = aDIGraph()
                        .register(aDIRequest()
                                .registerAs(Repo.class)
                                .dependsOn(File.class, JMomObjectMapper.class)
                                .create(diGraph -> new SnapshotFileRepo(diGraph.getBean(File.class), diGraph.getBean(JMomObjectMapper.class))))
                        .register(aDIRequest()
                                .dependsOn(ControlUnitRepository.class, Repo.class, JMomBus.class)
                                .create(diGraph -> new ControlUnitRepositoryHandler(diGraph.getBean(ControlUnitRepository.class), diGraph.getBean(Repo.class), diGraph.getBean(JMomBus.class))))
                        .register(aDIRequest()
                                .dependsOn(ConfigurationRepository.class, Repo.class)
                                .create(diGraph -> new ConfigurationRepositoryHandler(diGraph.getBean(ConfigurationRepository.class), diGraph.getBean(Repo.class))))
                        .register(aDIRequest()
                                .dependsOn(ControlUnit.class, JMomBus.class, JMomObjectMapper.class)
                                .create(diGraph -> new MQTTClientService(diGraph.getBean(ControlUnit.class), diGraph.getBean(JMomBus.class), diGraph.getBean(JMomObjectMapper.class)))
                                .onPostConstruct((obj, diGraph) -> ((MQTTClientService) obj).startAsync())
                                .onPreDestroy((obj, diGraph) -> ((MQTTClientService) obj).stopAsync()))
                        .register(aDIRequest()
                                .dependsOn(JMomBus.class, MQTTClientService.class)
                                .create(diGraph -> new JMomBusOverMQTTClient(diGraph.getBean(JMomBus.class), diGraph.getBean(MQTTClientService.class))))
                        .register(aDIRequest()
                                        .dependsOn(ControlUnit.class, Repo.class, MQTTClientService.class, JMomObjectMapper.class, JMomBus.class)
                                        .create(diGraph -> new SyncedRepoOverMQTTClient(
                                                diGraph.getBean(ControlUnit.class),
                                                diGraph.getBean(Repo.class),
                                                diGraph.getBean(MQTTClientService.class),
                                                diGraph.getBean(JMomObjectMapper.class),
                                                diGraph.getBean(JMomBus.class)
                                        ))
                        )
                ;
            }
            return diGraph;
        }
    }

    public static class Discovery {

        private static DIGraph diGraph;

        public static DIGraph diGraph() {
            if (diGraph == null) {
                diGraph = aDIGraph()
                        .register(aDIRequest()
                                .dependsOn(JMomBus.class, InterfaceDiscoverer.class)
                                .create(diGraph -> new InterfaceDiscoveryManager(diGraph.getBean(JMomBus.class), diGraph.getBeansAsSet(InterfaceDiscoverer.class)))
                                .onPostConstruct((obj, diGraph) -> ((InterfaceDiscoveryManager) obj).startDiscovery())
                                .onPreDestroy((obj, diGraph) -> ((InterfaceDiscoveryManager) obj).destroy()));
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
                                .dependsOn(ThingRepository.class, Repo.class)
                                .create(diGraph -> new ThingRepositoryHandler(diGraph.getBean(ThingRepository.class), diGraph.getBean(Repo.class))))
                        .register(aDIRequest()
                                .dependsOn(StateRepository.class, Repo.class)
                                .create(diGraph -> new StateRepositoryHandler(diGraph.getBean(StateRepository.class), diGraph.getBean(Repo.class))))
                        .register(aDIRequest()
                                .dependsOn(StateRepository.class)
                                .create(diGraph -> new DuplicateInterfaceStateChangedEventFilter(diGraph.getBean(StateRepository.class))))
                        .register(aDIRequest()
                                .dependsOn(CentralControlUnit.class, ConfigurationRepository.class, InterfaceProvider.class)
                                .create(diGraph -> new InterfaceProviderManager(diGraph.getBean(CentralControlUnit.class), diGraph.getBean(ConfigurationRepository.class), diGraph.getBeansAsSet(InterfaceProvider.class)))
                                .onPostConstruct((obj, diGraph) -> ((InterfaceProviderManager) obj).startInterfaces())
                                .onPreDestroy((obj, diGraph) -> ((InterfaceProviderManager) obj).stopInterfaces()));
            }
            return diGraph;
        }
    }

    public static void main(String[] args) {
        ConfigurationRepository bean = Init.diGraph.getBean(ConfigurationRepository.class);
        System.out.println(bean.getClass().getSimpleName());
        System.out.println(Init.diGraph.getUnfullfilledDependencies());
    }
}
