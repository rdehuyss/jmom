package org.jmom.core.infrastucture.bus;


import java.util.Set;

public class JMomBusRegistrar {

    private final JMomCommandBus commandBus;
    private final JMomEventBus eventBus;
    private final Set<JMomBusAware> toRegister;

    public JMomBusRegistrar(JMomCommandBus commandBus, JMomEventBus eventBus, Set<JMomBusAware> toRegister) {
        this.commandBus = commandBus;
        this.eventBus = eventBus;
        this.toRegister = toRegister;
    }

    public void doRegistration() {
        for (JMomBusAware busAware : toRegister) {
            commandBus.register(busAware);
            eventBus.register(busAware);
        }
    }

    public void register(Object object) {
        commandBus.register(object);
        eventBus.register(object);
    }
}
