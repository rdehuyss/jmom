package org.jmom.core.services.interfacing;

import org.jmom.core.infrastucture.bus.JMomBusInterceptor;
import org.jmom.core.infrastucture.eda.Message;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.things.StateRepository;

public class DuplicateInterfaceStateChangedEventFilter implements JMomBusInterceptor {

    private StateRepository stateRepository;

    public DuplicateInterfaceStateChangedEventFilter(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Override
    public boolean apply(Message message) {
        if(message instanceof StateChangedEvent) {
            StateChangedEvent newStateChangedEvent = (StateChangedEvent)message;
            StateChangedEvent currentStateChangedEvent = stateRepository.getLastStateChangedEvent(newStateChangedEvent.getDeviceIdentifier());

            if(currentStateChangedEvent != null &&
                    currentStateChangedEvent.getNewState().equals(newStateChangedEvent.getNewState())) {
                System.out.println("Filtering StateChangedEvent message as it is identical as the previous one.");
                return false;
            }
        }
        return true;
    }

}
