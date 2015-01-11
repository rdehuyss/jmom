package org.jmom.apps.android.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import butterknife.OnItemLongClick;
import com.google.common.base.Optional;
import com.google.common.util.concurrent.ServiceManager;
import org.jmom.apps.android.R;
import org.jmom.core.model.eda.commands.DeleteThingCommand;
import org.jmom.core.model.things.*;

//import javax.inject.Inject;

public class BrowseLocationActivity extends AbstractJMomActivity {

    public static final String PATH = "selectedPath";
    public static final String EDITING = "editing";
    private static final int EDIT_THING = 1;

//    @Inject
    ThingRepository thingRepository;

//    @Inject
    ServiceManager serviceManager;

    @InjectView(R.id.listview_items)
    ListView listView;

    @InjectView(R.id.breadcrumb)
    TextView breadcrumb;

    private MyAdapter adapter;

    // Tracks current contextual action mode
    private ActionMode currentActionMode;
    private MyAdapter.ViewHolder currentViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.browse_location_activity);

        if (!serviceManager.isHealthy()) {
            serviceManager.startAsync();
        }

        Path selectedPath = getSelectedPath();
        if (selectedPath.isRoot()) {
            breadcrumb.setVisibility(View.GONE);
        } else {
            breadcrumb.setText("You are here: " + selectedPath.toString());
        }

        adapter = new MyAdapter(this, thingRepository, selectedPath);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        listView.clearChoices();
        listView.requestLayout();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDIT_THING) {
            if (resultCode == RESULT_OK) {
                adapter.notifyDataSetChanged();
            }
        }
    }

    @OnItemClick(R.id.listview_items)
    public void onItemClick(View view) {
        MyAdapter.ViewHolder viewHolder = (MyAdapter.ViewHolder) view.getTag();
        Thing<?> thing = viewHolder.thing;
        browseChildLocationActivity(thing);
    }

    @OnItemLongClick(R.id.listview_items)
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position) {
        if (currentActionMode != null) { return false; }
        currentViewHolder = (MyAdapter.ViewHolder) view.getTag();
        currentActionMode = startActionMode(modeCallBack);
        listView.setItemChecked(position, true);
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browse_location_activity_menu, menu);
        return true;
    }

    private void browseChildLocationActivity(Thing thing) {
        Intent browseChildLocationActivity = new Intent(this, BrowseLocationActivity.class);
        browseChildLocationActivity.putExtra(PATH, thing.getPath().toString());
        this.startActivity(browseChildLocationActivity);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new) {
            Toast.makeText(this, "Boe", Toast.LENGTH_LONG).show();
            Intent createLocationActivity = new Intent(this, AddOrEditLocationActivity.class)
                    .putExtra(PATH, getSelectedPath().toString());
            startActivity(createLocationActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class MyAdapter extends BaseAdapter {
        private LayoutInflater layoutInflater;
        private ThingTree thingTree;

        public MyAdapter(Context context, ThingRepository thingRepository, Path path) {
            layoutInflater = LayoutInflater.from(context);
            Optional<?> thing = thingRepository.getByPath(path);
            if (thing.isPresent() && thing.get() instanceof ThingTree) {
                thingTree = (ThingTree) thing.get();
            }
        }

        @Override
        public int getCount() {
            return thingTree.getChildren().size();
        }

        @Override
        public Thing getItem(int position) {
            return (Thing) thingTree.getChildren().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            ViewHolder holder;
            if (view != null) {
                holder = (ViewHolder) view.getTag();
            } else {
                view = layoutInflater.inflate(R.layout.browse_listitem_view, parent, false);
                holder = new ViewHolder(view);
                view.setTag(holder);
            }

            Thing thing = getItem(position);
            holder.thing = thing;
            holder.name.setText(thing.getName());
            holder.description.setText(thing.getDescription());

            return view;
        }


        static class ViewHolder {
            Thing<?> thing;

            @InjectView(R.id.icon)
            ImageView icon;
            @InjectView(R.id.firstLine)
            TextView name;
            @InjectView(R.id.secondLine)
            TextView description;
            @InjectView(R.id.on_off_button)
            ToggleButton toggleButton;

            public ViewHolder(View view) {
                ButterKnife.inject(this, view);
                if(thing instanceof Location) {
                    toggleButton.setVisibility(View.GONE);
                }
            }
        }
    }

    private ActionMode.Callback modeCallBack = new ActionMode.Callback() {
        // Called when the action mode is created; startActionMode() was called
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.setTitle("Actions");
            String[] menuItems = getResources().getStringArray(R.array.menu);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
            return true;
        }

        // Called each time the action mode is shown.
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        // Called when the user selects a contextual menu item
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int menuItemIndex = item.getItemId();
            if(menuItemIndex == 0) {
                Intent createLocationActivity = new Intent(BrowseLocationActivity.this, AddOrEditLocationActivity.class)
                        .putExtra(PATH, currentViewHolder.thing.getPath().toString())
                        .putExtra(EDITING, true);
                startActivityForResult(createLocationActivity, EDIT_THING);
                mode.finish();
                return true;
            } else if (menuItemIndex == 1) {
                jMomBus.post(new DeleteThingCommand(currentViewHolder.thing.getPath()));
                adapter.notifyDataSetChanged();
                mode.finish();
                return true;
            }
            return false;
        }

        // Called when the user exits the action mode
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            listView.clearChoices();
            listView.requestLayout();
            currentViewHolder = null;
            currentActionMode = null; // Clear current action mode
        }
    };
}
