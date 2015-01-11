package org.jmom.apps.server;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ServiceManager;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.controlunit.ControlUnitRepository;
import org.jmom.core.model.eda.commands.ChangeStateCommand;
import org.jmom.core.model.eda.commands.CreateControlUnitCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;

import java.io.IOException;

public class TestClass {

    private ControlUnitRepository controlUnitRepository;
    private ConfigurationRepository configurationRepository;
    private ThingRepository thingRepository;
    private StateRepository stateRepository;
    private JMomBus jMomBus;
    private ServiceManager serviceManager;

    public static void main(String[] args) throws IOException {
        System.out.println("Testclass found!");
//        testClass.doStateChange(new ChangeStateCommand(new DeviceIdentifier("RFXCom-LIGHTING1-ARC-L-5"), OnOffChange.ON));
    }

    public TestClass(ControlUnitRepository controlUnitRepository, ConfigurationRepository configurationRepository, ThingRepository thingRepository, StateRepository stateRepository, JMomBus jMomBus, ServiceManager serviceManager) {
        this.controlUnitRepository = controlUnitRepository;
        this.configurationRepository = configurationRepository;
        this.thingRepository = thingRepository;
        this.stateRepository = stateRepository;
        this.jMomBus = jMomBus;
        this.serviceManager = serviceManager;


        if (!controlUnitRepository.isControlUnitConfigured()) {
            jMomBus.post(new CreateControlUnitCommand(new CentralControlUnit("ronald.dehuysser@gmail.com", "testen", "Central Control Unit")));
        }

        serviceManager.startAsync();
        serviceManager.awaitHealthy();

    }


    @Subscribe
    public void stateChanged(StateChangedEvent deviceFoundEvent) {
        System.out.println("Received a new DeviceFoundEvent! " + deviceFoundEvent.getDeviceIdentifier() + "; stateChange: " + deviceFoundEvent.getNewState());
    }

    public void doStateChange(ChangeStateCommand command) {
        jMomBus.post(command);
    }

}
