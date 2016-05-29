package us.mikeandwan.photos.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.data.Category;
import us.mikeandwan.photos.data.MawDataManager;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.tasks.BackgroundTask;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.GetRecentCategoriesBackgroundTask;

@SuppressLint("Registered")
@OptionsMenu(R.menu.mode_selection)
@EActivity(R.layout.activity_mode_selection)
public class ModeSelectionActivity extends AppCompatActivity {
    private SimpleExpandableListAdapter _adapter;
    private List<Map<String, String>> _groupData = new ArrayList<>();
    private List<List<Map<String, String>>> _childData = new ArrayList<>();
    private MawDataManager _dm;
    private List<Map<String, String>> _yearChildren = new ArrayList<>();
    private List<Integer> _yearList;

    @OptionsMenuItem(R.id.action_settings)
    protected MenuItem _refreshMenuItem;

    @ViewById(R.id.toolbar)
    protected Toolbar _toolbar;

    @ViewById(R.id.modeExpandableListView)
    protected ExpandableListView _modeExpandableListView;

    @App
    MawApplication _app;


    @AfterInject
    protected void afterInject() {
        _dm = new MawDataManager(_app);
    }


    @AfterViews
    protected void afterViews() {
        if (_toolbar != null) {
            setSupportActionBar(_toolbar);

            ViewCompat.setElevation(_toolbar, 8);
        }

        _modeExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Intent intent;

                if (groupPosition == 0) {
                    intent = new Intent(parent.getContext(), CategoryListActivity_.class);
                    intent.putExtra("YEAR", Integer.parseInt(_childData.get(groupPosition).get(childPosition).get("NAME")));
                } else {
                    intent = new Intent(parent.getContext(), PhotoListActivity_.class);
                    intent.putExtra("NAME", _childData.get(groupPosition).get(childPosition).get("NAME"));
                }

                intent.putExtra("URL", _childData.get(groupPosition).get(childPosition).get("URL"));

                startActivity(intent);

                return true;
            }
        });

        _adapter = new SimpleExpandableListAdapter(this,
            _groupData,
            android.R.layout.simple_expandable_list_item_1,
            new String[]{"NAME"},
            new int[]{android.R.id.text1},
            _childData,
            android.R.layout.simple_expandable_list_item_2,
            new String[]{"NAME"},
            new int[]{android.R.id.text1});

        initModeList();
    }


    @Override
    public void onRestart() {
        // we do this on restart, so that if a new year was pulled during a refresh of the category
        // screen, or poller, this page will show the latest years we have available automatically
        prepareYearChildren();

        _adapter.notifyDataSetChanged();

        super.onRestart();
    }


    @OptionsItem(R.id.action_settings)
    protected void onMenuItemSettings() {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    @OptionsItem(R.id.action_force_sync)
    protected void onMenuItemForceSync() {
        forceSync();
    }


    private void forceSync() {
        BackgroundTask task = new GetRecentCategoriesBackgroundTask(getBaseContext()) {
            @Override
            protected void postExecuteTask(List<Category> result) {
                onSyncComplete(result);
            }

            @Override
            protected void handleException(ExecutionException ex) {
                _refreshMenuItem.getActionView().clearAnimation();
                _refreshMenuItem.setActionView(null);

                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getBaseContext(), LoginActivity_.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ImageView iv = (ImageView) inflater.inflate(R.layout.refresh_indicator, null);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        _refreshMenuItem.setActionView(iv);
    }


    private void onSyncComplete(List<Category> result) {
        // go back to the database rather than inspecting results, as there poller may have happened
        // before the sync, which means that the new year would not be in the list of results
        prepareYearChildren();

        _adapter.notifyDataSetChanged();

        _refreshMenuItem.getActionView().clearAnimation();
        _refreshMenuItem.setActionView(null);
    }


    private void prepareYearChildren() {
        List<Integer> years = _dm.getPhotoYears();

        // if we are showing this for the first time, or have a new year available in the data,
        // show it in the interface
        if (_yearList == null || years.size() != _yearList.size()) {
            _yearList = years;

            _yearChildren.clear();

            for (Integer year : _yearList) {
                _yearChildren.add(createChild(year, PhotoApiClient.getCategoriesForYearUrl(year)));
            }
        }
    }


    private void initModeList() {
        List<Map<String, String>> children;

        // by year
        prepareYearChildren();
        _groupData.add(createGroup("By Year"));
        _childData.add(_yearChildren);

        // by comments
        children = new ArrayList<>();
        _groupData.add(createGroup("By Comments"));
        _childData.add(children);

        children.add(createChild("Newest", PhotoApiClient.getPhotosByCommentDateUrl(true)));
        children.add(createChild("Oldest", PhotoApiClient.getPhotosByCommentDateUrl(false)));
        children.add(createChild("Your Newest", PhotoApiClient.getPhotosByUserCommentDateUrl(true)));
        children.add(createChild("Your Oldest", PhotoApiClient.getPhotosByUserCommentDateUrl(false)));
        children.add(createChild("Most Comments", PhotoApiClient.getPhotosByCommentCountUrl(true)));
        children.add(createChild("Least Comments", PhotoApiClient.getPhotosByCommentCountUrl(false)));

        // by ratings
        children = new ArrayList<>();
        _groupData.add(createGroup("By Rating"));
        _childData.add(children);

        children.add(createChild("Average Rating", PhotoApiClient.getPhotosByAverageRatingUrl()));
        children.add(createChild("Your Rating", PhotoApiClient.getPhotosByUserRatingUrl()));

        // random
        children = new ArrayList<>();
        _groupData.add(createGroup("Random"));
        _childData.add(children);

        children.add(createChild("Surprise Me!", "random"));

        _modeExpandableListView.setAdapter(_adapter);
    }


    private Map<String, String> createGroup(String name) {
        Map<String, String> group = new HashMap<>();
        group.put("NAME", name);

        return group;
    }


    private Map<String, String> createChild(int name, String url) {
        return createChild(String.valueOf(name), url);
    }


    private Map<String, String> createChild(String name, String url) {
        Map<String, String> child = new HashMap<>();
        child.put("NAME", name);
        child.put("URL", url);

        return child;
    }
}
