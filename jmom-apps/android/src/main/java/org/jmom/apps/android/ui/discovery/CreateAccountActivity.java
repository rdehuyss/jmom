package org.jmom.apps.android.ui.discovery;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.OnClick;
import com.google.common.eventbus.Subscribe;
import org.jmom.apps.android.R;
import org.jmom.apps.android.ui.AbstractJMomActivity;
import org.jmom.apps.android.ui.BrowseLocationActivity;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.controlunit.RemoteControlUnit;
import org.jmom.core.model.eda.commands.CreateControlUnitCommand;
import org.jmom.core.model.things.Thing;
import org.jmom.core.services.remoting.client.RepoSyncedEvent;

public class CreateAccountActivity extends AbstractJMomActivity {

    @InjectView(R.id.editText_email)
    EditText editTextEmail;
    @InjectView(R.id.editText_password)
    EditText editTextPassword;
    @InjectView(R.id.editText_device_name)
    EditText editTextDeviceName;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.create_account_activity);

    }

    @OnClick(R.id.button_save)
    public void onSave() {
        jMomBus.register(this);

        String emailAddress = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String deviceName = editTextDeviceName.getText().toString();
        RemoteControlUnit controlUnit = new RemoteControlUnit(emailAddress, password, deviceName);
        jMomBus.post(new CreateControlUnitCommand(controlUnit));
        getJMomApplication().refreshDiGraph();

        progressDialog = ProgressDialog.show(this, "Loading", "Please wait while we download your configuration...");
    }

    @Subscribe
    public void onRepoSynced(RepoSyncedEvent repoSyncedEvent) {
        jMomBus.unregister(this);
        runOnUiThread(() -> {
            progressDialog.hide();
            if(repoSyncedEvent.isDataSynced()) {
                Toast.makeText(this, "We successfully downloaded your configuration", Toast.LENGTH_LONG).show();
                if(getBean(ConfigurationRepository.class).hasUnknownCentralControlUnitConfiguration()) {
                    Intent createResidenceActivity = new Intent(this, CreateResidenceActivity.class);
                    this.startActivity(createResidenceActivity);
                }
            } else {
                Toast.makeText(this, "An error occurred syncing your configuration. Is the Central Control Unit running?", Toast.LENGTH_LONG).show();
            }
        });
    }

}
