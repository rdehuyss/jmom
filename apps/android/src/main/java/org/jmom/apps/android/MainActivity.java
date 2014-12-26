package org.jmom.apps.android;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.google.common.collect.JMomFluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import dagger.ObjectGraph;
import org.jmom.core.infrastucture.bus.JMomBusRegistrar;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        ObjectGraph objectGraph = ObjectGraph.create(new AndroidModule());
        JMomBusRegistrar registrar = objectGraph.get(JMomBusRegistrar.class);
        registrar.doRegistration();

        TestClass testClass = objectGraph.get(TestClass.class);
        testClass.setMainActivity(this);
        Toast.makeText(this, "Successfully intialized!", Toast.LENGTH_LONG).show();
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            StringBuilder result = new StringBuilder();

            JMomFluentIterable.from(Lists.newArrayList("1 ", "2 ", "3 ")).forEachItem(s -> result.append(s));
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final Button button = (Button) rootView.findViewById(R.id.test_btn);

            button.setOnClickListener(v -> Toast.makeText(v.getContext(), "Text to show: " + result.toString(), Toast.LENGTH_LONG).show());
            return rootView;
        }

    }
}
