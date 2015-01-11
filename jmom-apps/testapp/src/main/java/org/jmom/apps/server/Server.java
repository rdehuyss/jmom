package org.jmom.apps.server;

import com.google.common.eventbus.Subscribe;
import org.jmom.core.infrastucture.DIGraph;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.eda.Message;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.controlunit.ControlUnitRepository;
import org.jmom.core.model.eda.commands.CreateControlUnitCommand;
import org.jmom.core.model.eda.commands.LinkCentralControlUnitToResidenceCommand;
import org.jmom.core.model.eda.commands.SaveThingCommand;
import org.jmom.core.model.eda.events.InterfaceDiscoveryFinishedEvent;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.Residence;
import org.jmom.core.services.remoting.client.RepoSyncedEvent;

import java.io.IOException;

public class Server {

    private static DIGraph diGraph;
    private static DIGraph discoveryGraph;
    private static DIGraph runtimeGraph;

    public static void main(String[] args) throws IOException {
        diGraph = ServerModule.Init.diGraph();

        Server server = new Server(diGraph.getBean(JMomBus.class), diGraph.getBean(ControlUnitRepository.class));
    }

    public Server(JMomBus jMomBus, ControlUnitRepository controlUnitRepository) {
        jMomBus.register(this);

        if (!controlUnitRepository.isControlUnitConfigured()) {
            CentralControlUnit controlUnit = new CentralControlUnit("ronald.dehuysser@gmail.com", "testen", "Central Control Unit");
            jMomBus.post(new CreateControlUnitCommand(controlUnit));
            diGraph.resolveDiRequests();
            jMomBus.waitFor(RepoSyncedEvent.class, 12);

            discoveryGraph = ServerModule.Discovery.diGraph();
            System.out.println("Starting discovery!");
        } else {
            ConfigurationRepository configurationRepository = diGraph.getBean(ConfigurationRepository.class);
            CentralControlUnit centralControlUnit = (CentralControlUnit) controlUnitRepository.getControlUnit();
            if (configurationRepository.isCentralControlUnitConfigured(centralControlUnit)) {
                runtimeGraph = ServerModule.Runtime.diGraph();
                System.out.println("We're up & running...");
            }
            System.out.println(configurationRepository);
        }
    }

    @Subscribe
    public void onMessage(Message message) {
        System.out.println(">>> Message arrived: " + message.getClass().getSimpleName());
    }

    @Subscribe
    public void onDiscoveryFinished(InterfaceDiscoveryFinishedEvent discoveryFinishedEvent) {
        discoveryGraph.destroy();
    }

    @Subscribe
    public void onLinkCentralControlUnitToResidenceCommand(LinkCentralControlUnitToResidenceCommand linkCentralControlUnitToResidenceCommand) {
        runtimeGraph = ServerModule.Runtime.diGraph();
        System.out.println("We're up & running...");
    }
}
