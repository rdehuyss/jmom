package org.jmom.apps.android.ui;

import android.content.Intent;
import android.os.Bundle;
import org.jmom.apps.android.ui.discovery.CreateAccountActivity;
import org.jmom.core.model.controlunit.ControlUnitRepository;

import javax.inject.Inject;

public class InitActivity extends AbstractJMomActivity {

    @Inject
    private ControlUnitRepository controlUnitRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!controlUnitRepository.isControlUnitConfigured()) {
            Intent startConfigurationActivity = new Intent(this, CreateAccountActivity.class);
            this.startActivity(startConfigurationActivity);
        } else {
            Intent startBrowseLocationActivity = new Intent(this, BrowseLocationActivity.class);
            this.startActivity(startBrowseLocationActivity);
        }

        finish();
    }
}
