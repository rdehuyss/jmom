package org.jmom.apps.android;

import android.widget.Toast;
import com.google.common.eventbus.Subscribe;
import com.google.common.util.concurrent.ServiceManager;
import org.jmom.apps.android.ui.MainActivity;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.model.eda.commands.ChangeStateCommand;
import org.jmom.core.model.eda.events.StateChangedEvent;
import org.jmom.core.model.things.StateRepository;
import org.jmom.core.model.things.ThingRepository;

/**
 * Created by rdehuyss on 25.12.14.
 */
public class TestClass {

    private final ThingRepository thingRepository;
    private final StateRepository stateRepository;
    private final JMomBus jMomBus;
    private final ServiceManager serviceManager;

    private MainActivity mainActivity;

    public TestClass(ThingRepository thingRepository, StateRepository stateRepository, JMomBus jMomBus, ServiceManager serviceManager) {
        this.thingRepository = thingRepository;
        this.stateRepository = stateRepository;
        this.jMomBus = jMomBus;
        this.serviceManager = serviceManager;


        serviceManager.startAsync();
        serviceManager.awaitHealthy();
    }


    @Subscribe
    public void stateChanged(StateChangedEvent stateChangedEvent) {
        if (mainActivity != null) {
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

    public void doStateChange(ChangeStateCommand command) {
        jMomBus.post(command);
    }
}
