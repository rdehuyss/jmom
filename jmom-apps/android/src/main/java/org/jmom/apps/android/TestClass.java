package org.jmom.apps.android;

import android.widget.Toast;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ServiceManager;
import org.jmom.core.infrastucture.bus.JMomBusRegistrar;
import org.jmom.core.model.eda.StateChangedByInterfaceEvent;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;

import javax.inject.Inject;

/**
 * Created by rdehuyss on 25.12.14.
 */
public class TestClass {

    private final ThingRepository thingRepository;
    private final StateRepository stateRepository;
    private final JMomBusRegistrar jMomBusRegistrar;
    private final ServiceManager serviceManager;

    private MainActivity mainActivity;

    @Inject
    public TestClass(ThingRepository thingRepository, StateRepository stateRepository, JMomBusRegistrar jMomBusRegistrar, ServiceManager serviceManager) {
        this.thingRepository = thingRepository;
        this.stateRepository = stateRepository;
        this.jMomBusRegistrar = jMomBusRegistrar;
        this.serviceManager = serviceManager;

        jMomBusRegistrar.register(this);

        serviceManager.startAsync();
        serviceManager.awaitHealthy();
    }


    @Subscribe
    public void stateChanged(StateChangedByInterfaceEvent stateChangedEvent) {
        if(mainActivity != null) {
            mainActivity.runOnUiThread(new Runnable() {
                public void run() {
                    Toast.makeText(mainActivity, "Event received: " + stateChangedEvent, Toast.LENGTH_SHORT).show();
                }
            });

        }
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
