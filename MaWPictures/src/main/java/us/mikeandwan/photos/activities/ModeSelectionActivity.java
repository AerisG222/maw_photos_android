package us.mikeandwan.photos.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.SimpleExpandableListAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.tasks.GetYearsTask;


public class ModeSelectionActivity extends AppCompatActivity {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final List<Map<String, String>> _groupData = new ArrayList<>();
    private final List<List<Map<String, String>>> _childData = new ArrayList<>();
    private final List<Map<String, String>> _yearChildren = new ArrayList<>();
    private SimpleExpandableListAdapter _adapter;
    private List<Integer> _yearList;
    private MenuItem _refreshMenuItem;

    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.modeExpandableListView) ExpandableListView _modeExpandableListView;

    @Bean
    MawDataManager _dm;

    @Bean
    GetYearsTask _getYearsTask;


    protected void afterBind() {
        if (_toolbar != null) {
            setSupportActionBar(_toolbar);
            ViewCompat.setElevation(_toolbar, 8);
        }

        _modeExpandableListView.setOnChildClickListener((parent, v, groupPosition, childPosition, id) ->
                onItemClicked(parent, v, groupPosition, childPosition, id)
        );

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        ButterKnife.bind(this);
        afterBind();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mode_selection, menu);

        _refreshMenuItem = menu.findItem(R.id.action_settings);

        return true;
    }


    @Override
    public void onRestart() {
        // we do this on restart, so that if a new year was pulled during a refresh of the category
        // screen, or poller, this page will show the latest years we have available automatically
        prepareYearChildren();

        _adapter.notifyDataSetChanged();

        super.onRestart();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
    }


    public void onMenuItemSettings(MenuItem menuItem) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    public void onMenuItemForceSync(MenuItem menuItem) {
        forceSync();
    }


    private boolean onItemClicked(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
        Intent intent;

        if (groupPosition == 0) {
            intent = new Intent(parent.getContext(), CategoryListActivity.class);
            intent.putExtra("YEAR", Integer.parseInt(_childData.get(groupPosition).get(childPosition).get("NAME")));
        } else {
            intent = new Intent(parent.getContext(), PhotoListActivity.class);
            intent.putExtra("NAME", _childData.get(groupPosition).get(childPosition).get("NAME"));
        }

        intent.putExtra("URL", _childData.get(groupPosition).get(childPosition).get("URL"));

        startActivity(intent);

        return true;
    }


    private void forceSync() {
        disposables.add(
            Flowable.fromCallable(() -> _getYearsTask.call())
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.single())
            .subscribe(
                x -> onSyncComplete(x),
                ex -> {
                    _refreshMenuItem.getActionView().clearAnimation();
                    _refreshMenuItem.setActionView(null);

                    if (ex.getCause() instanceof MawAuthenticationException) {
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                    }
            })
        );

        ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.refresh_indicator, _toolbar, false);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        _refreshMenuItem.setActionView(iv);
    }


    private void onSyncComplete(List<Integer> years) {
        // go back to the database rather than inspecting results, as there poller may have happened
        // before the sync, which means that the new year would not be in the list of results
        prepareYearChildren(years);

        _adapter.notifyDataSetChanged();

        _refreshMenuItem.getActionView().clearAnimation();
        _refreshMenuItem.setActionView(null);
    }


    private void prepareYearChildren() {
        List<Integer> years = _dm.getPhotoYears();

        prepareYearChildren(years);
    }


    private void prepareYearChildren(List<Integer> years) {
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
