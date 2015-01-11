package org.jmom.apps.android.ui.discovery;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.OnClick;
import org.jmom.apps.android.R;
import org.jmom.apps.android.ui.AbstractJMomActivity;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.controlunit.CentralControlUnit;
import org.jmom.core.model.controlunit.RemoteControlUnit;
import org.jmom.core.model.eda.commands.CreateControlUnitCommand;
import org.jmom.core.model.eda.commands.LinkCentralControlUnitToResidenceCommand;
import org.jmom.core.model.eda.commands.SaveThingCommand;
import org.jmom.core.model.things.Path;
import org.jmom.core.model.things.Residence;

import javax.inject.Inject;

public class CreateResidenceActivity extends AbstractJMomActivity {

    @Inject
    private ConfigurationRepository configurationRepository;

    @InjectView(R.id.textView_centralControlConfigurationFound)
    TextView centralControlUnitConfigurationFound;
    @InjectView(R.id.editText_residence_name)
    EditText editTextResidenceName;
    @InjectView(R.id.editText_residence_description)
    EditText editTextResidenceDescription;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.create_residence_activity);

        CentralControlUnit centralControlUnit = configurationRepository.getUnknownCentralControlUnitConfigurations().iterator().next();
        centralControlUnitConfigurationFound.setText("A new Central Control Unit Configuration named '" + centralControlUnit.getControlUnitName() + "' was found. A Central Control Unit belongs to a certain residence. Here you can create a residence where that central control unit is located.");
    }

    @OnClick(R.id.button_save)
    public void onSave() {
        String residenceName = editTextResidenceName.getText().toString();
        String residenceDescription = editTextResidenceDescription.getText().toString();

        Residence home = new Residence(residenceName, residenceDescription);
        jMomBus.post(new SaveThingCommand(Path.root(), home));
        CentralControlUnit centralControlUnit = configurationRepository.getUnknownCentralControlUnitConfigurations().iterator().next();
        jMomBus.post(new LinkCentralControlUnitToResidenceCommand(centralControlUnit, home));
    }
}
