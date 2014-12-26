package org.jmom.apps.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.google.common.collect.JMomFluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dagger.ObjectGraph;
import org.jmom.core.infrastucture.bus.JMomBusRegistrar;
import org.jmom.core.model.eda.ChangeStateCommand;
import org.jmom.core.model.things.devices.DeviceIdentifier;
import org.jmom.core.model.things.devices.typelibrary.OnOffChange;


public class MainActivity extends Activity {

    private TestClass testClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        ObjectGraph objectGraph = ObjectGraph.create(new AndroidModule());
        JMomBusRegistrar registrar = objectGraph.get(JMomBusRegistrar.class);
        registrar.doRegistration();

        testClass = objectGraph.get(TestClass.class);
        testClass.setMainActivity(this);
        Toast.makeText(this, "Successfully intialized!", Toast.LENGTH_LONG).show();
        ButterKnife.inject(this);
    }

    @OnClick(R.id.btn_kerstlamp)
    public void setOn(Button button) {
        if("Off".equals(button.getText())) {
            testClass.doStateChange(new ChangeStateCommand(new DeviceIdentifier("RFXCom-LIGHTING1-ARC-L-5"), OnOffChange.OFF));
            button.setText("On");
        } else {
            testClass.doStateChange(new ChangeStateCommand(new DeviceIdentifier("RFXCom-LIGHTING1-ARC-L-5"), OnOffChange.ON));
            button.setText("Off");
        }
    }
}
