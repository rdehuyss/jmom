package org.jmom.core.model.eda.events;

import org.jmom.core.infrastucture.bus.JMomBusInterceptor;
import org.jmom.core.infrastucture.eda.Message;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.controlunit.ControlUnitRepository;

public class CentralControlUnitJMomBusInterceptor implements JMomBusInterceptor {

    private CentralControlUnit centralControlUnit;

    public CentralControlUnitJMomBusInterceptor(CentralControlUnit centralControlUnit) {
        this.centralControlUnit = centralControlUnit;
    }

    @Override
    public boolean apply(Message input) {
        if (input instanceof InterfaceProviderEvent) {
            ((InterfaceProviderEvent) input).setCentralControlUnit(centralControlUnit);
        }

        return true;
    }
}
