package org.jmom.apps.android.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import butterknife.ButterKnife;
import com.google.common.eventbus.Subscribe;
import org.jmom.apps.android.JMomApplication;
import org.jmom.core.infrastucture.bus.JMomBus;
import org.jmom.core.infrastucture.eda.ErrorMessage;
import org.jmom.core.model.configuration.ConfigurationRepository;
import org.jmom.core.model.things.Path;

import javax.inject.Inject;

public class AbstractJMomActivity extends Activity {

    @Inject
    protected JMomBus jMomBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    protected void onCreate(Bundle savedInstanceState, int layoutResID) {
        super.onCreate(savedInstanceState);
        init();
        setContentView(layoutResID);
        ButterKnife.inject(this);
    }

    protected Path getSelectedPath() {
        Intent intent = getIntent();
        String selectedPathAsString = intent.getStringExtra(BrowseLocationActivity.PATH);
        if (selectedPathAsString != null) {
            return Path.fromString(selectedPathAsString);
        } else {
            return Path.root();
        }
    }

    private void init() {
        getJMomApplication().inject(this);
        jMomBus.register(this);
    }

    public <T> T getBean(Class<T> clazz) {
        return getJMomApplication().getBean(clazz);
    }

    @Subscribe
    public void onErrorMessage(ErrorMessage errorMessage) {
        runOnUiThread(() -> {
            Toast.makeText(this, errorMessage.getReason(), Toast.LENGTH_LONG).show();
        });
    }

    public JMomApplication getJMomApplication() {
        return (JMomApplication) getApplication();
    }
}
