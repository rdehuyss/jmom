package org.jmom.apps.server;

import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ServiceManager;
import dagger.ObjectGraph;
import org.jmom.core.infrastucture.bus.JMomBusRegistrar;
import org.jmom.core.infrastucture.bus.JMomCommandBus;
import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.core.model.eda.StateChangedEvent;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.typelibrary.OnOffChange;

import javax.inject.Inject;
import java.io.IOException;

public class TestClass {

    private ThingRepository thingRepository;
    private StateRepository stateRepository;
    private JMomCommandBus commandBus;
    private JMomBusRegistrar jMomBusRegistrar;
    private ServiceManager serviceManager;

    public static void main(String[] args) throws IOException {
        ObjectGraph objectGraph = ObjectGraph.create(new ServerModule());
        JMomBusRegistrar registrar = objectGraph.get(JMomBusRegistrar.class);
        registrar.doRegistration();

        TestClass testClass = objectGraph.get(TestClass.class);
        System.out.println("Testclass found!");
        testClass.doStateChange(new ChangeStateCommand(new DeviceIdentifier("RFXCom-LIGHTING1-ARC-L-5"), OnOffChange.ON));
    }

    @Inject
    public TestClass(ThingRepository thingRepository, StateRepository stateRepository, JMomCommandBus commandBus, JMomBusRegistrar jMomBusRegistrar, ServiceManager serviceManager) {
        this.thingRepository = thingRepository;
        this.stateRepository = stateRepository;
        this.commandBus = commandBus;
        this.jMomBusRegistrar = jMomBusRegistrar;
        this.serviceManager = serviceManager;

        jMomBusRegistrar.register(this);

        serviceManager.startAsync();
        serviceManager.awaitHealthy();

    }


    @Subscribe
    public void stateChanged(StateChangedEvent deviceFoundEvent) {
        System.out.println("Received a new DeviceFoundEvent! " + deviceFoundEvent.getDeviceIdentifier() + "; stateChange: " + deviceFoundEvent.getNewState());
    }

    public void doStateChange(ChangeStateCommand command) {
        commandBus.post(command);
    }

}
