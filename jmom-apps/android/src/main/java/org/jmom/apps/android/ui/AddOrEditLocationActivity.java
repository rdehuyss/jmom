package org.jmom.apps.android.ui;

import android.os.Bundle;
import android.widget.EditText;
import butterknife.InjectView;
import butterknife.OnClick;
import org.jmom.apps.android.R;
import org.jmom.core.infrastucture.eda.Command;
import org.jmom.core.model.eda.commands.SaveThingCommand;
import org.jmom.core.model.eda.commands.UpdateThingCommand;
import org.jmom.core.model.things.Location;
import org.jmom.core.model.things.Thing;
import org.jmom.core.model.things.ThingRepository;

//import javax.inject.Inject;

public class AddOrEditLocationActivity extends AbstractJMomActivity {

  //  @Inject
    ThingRepository thingRepository;

    @InjectView(R.id.editText_name)
    EditText editTextName;
    @InjectView(R.id.editText_description)
    EditText editTextDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.add_or_edit_location_activity);
        if(getIntent().getBooleanExtra(BrowseLocationActivity.EDITING, false)) {
            Thing thing = thingRepository.getByPath(getSelectedPath()).get();
            editTextName.setText(thing.getName());
            editTextDescription.setText(thing.getDescription());
        }
    }

    @OnClick(R.id.button_save)
    public void onSave() {
        Location location = new Location(editTextName.getText().toString(), editTextDescription.getText().toString());
        Command command = null;
        if(getIntent().getBooleanExtra(BrowseLocationActivity.EDITING, false)) {
            command = new UpdateThingCommand(getSelectedPath(), getSelectedPath().getParent(), location);
        } else {
            command = new SaveThingCommand(getSelectedPath(), location);
        }
        jMomBus.post(command);
        setResult(RESULT_OK);
        finish();
    }

}
