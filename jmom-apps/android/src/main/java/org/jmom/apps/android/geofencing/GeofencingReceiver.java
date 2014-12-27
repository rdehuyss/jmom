package org.jmom.apps.android.geofencing;

import android.util.Log;
import org.jmom.apps.android.NotificationUtils;
import org.jmom.apps.android.R;

public class GeofencingReceiver extends ReceiveGeofenceTransitionIntentService {

    @Override
    protected void onEnteredGeofences(String[] strings) {
        Log.d(GeofencingReceiver.class.getName(), "onEnter");

        NotificationUtils.sendNotification(this, 1, R.drawable.ic_launcher, "Entered home", "Entered home", "You just entered your home!");
    }

    @Override
    protected void onExitedGeofences(String[] strings) {
        Log.d(GeofencingReceiver.class.getName(), "onExit");

        NotificationUtils.sendNotification(this, 1, R.drawable.ic_launcher, "Exited home", "Exited home", "You just exited your home!");
    }

    @Override
    protected void onError(int i) {
        Log.e(GeofencingReceiver.class.getName(), "Error: " + i);
    }
}