package org.jmom.apps.android;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import org.jmom.core.infrastucture.DIGraph;

import java.util.Arrays;
import java.util.List;

import static org.jmom.core.infrastucture.DIGraph.aDIGraph;

public class JMomApplication extends Application {

    private DIGraph objectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        System.setProperty("org.joda.time.DateTimeZone.Provider", "org.joda.time.tz.UTCProvider");

        objectGraph = aDIGraph()
                .basedOn(AndroidModule.Runtime.diGraph())
                .register(Context.class, this);
    }

    public void inject(Object object) {
        objectGraph.inject(object, Activity.class);
    }

    public void refreshDiGraph() {
        objectGraph.resolveDiRequests();
    }

    public <T> T getBean(Class<T> clazz) {
        return objectGraph.getBean(clazz);
    }
}
