package us.mikeandwan.photos.ui.mode;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.PhotoListType;
import us.mikeandwan.photos.ui.settings.SettingsActivity;
import us.mikeandwan.photos.di.DaggerTaskComponent;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.services.MawDataManager;
import us.mikeandwan.photos.tasks.GetYearsTask;
import us.mikeandwan.photos.ui.BaseActivity;
import us.mikeandwan.photos.ui.categories.CategoryListActivity;
import us.mikeandwan.photos.ui.HasComponent;
import us.mikeandwan.photos.ui.photos.PhotoListActivity;


public class ModeSelectionActivity extends BaseActivity implements HasComponent<TaskComponent> {
    private static final String KEY_NAME = "NAME";
    private static final String KEY_TYPE = "TYPE";

    private final CompositeDisposable _disposables = new CompositeDisposable();
    private final List<Map<String, String>> _groupData = new ArrayList<>();
    private final List<List<Map<String, String>>> _childData = new ArrayList<>();
    private final List<Map<String, String>> _yearChildren = new ArrayList<>();
    private SimpleExpandableListAdapter _adapter;
    private List<Integer> _yearList;
    private MenuItem _refreshMenuItem;
    private TaskComponent _taskComponent;

    @BindView(R.id.toolbar) Toolbar _toolbar;
    @BindView(R.id.modeExpandableListView) ExpandableListView _modeExpandableListView;

    @Inject MawDataManager _dm;
    @Inject GetYearsTask _getYearsTask;
    @Inject AuthenticationExceptionHandler _authHandler;


    public TaskComponent getComponent() {
        return _taskComponent;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mode_selection);
        ButterKnife.bind(this);

        _taskComponent = DaggerTaskComponent.builder()
                .applicationComponent(getApplicationComponent())
                .taskModule(getTaskModule())
                .build();

        _taskComponent.inject(this);

        if (_toolbar != null) {
            setSupportActionBar(_toolbar);
            ViewCompat.setElevation(_toolbar, 8);
        }

        _modeExpandableListView.setOnChildClickListener(this::onItemClicked);

        _adapter = new SimpleExpandableListAdapter(this,
                _groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{KEY_NAME},
                new int[]{android.R.id.text1},
                _childData,
                android.R.layout.simple_expandable_list_item_2,
                new String[]{KEY_NAME},
                new int[]{android.R.id.text1});

        initModeList();
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
        _disposables.clear(); // do not send event after activity has been destroyed
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

        Map<String, String> child = _childData.get(groupPosition).get(childPosition);
        PhotoListType type = PhotoListType.valueOf(child.get(KEY_TYPE));

        if (type == PhotoListType.ByCategory) {
            intent = new Intent(parent.getContext(), CategoryListActivity.class);
            intent.putExtra("YEAR", Integer.parseInt(child.get(KEY_NAME)));
        } else {
            intent = new Intent(parent.getContext(), PhotoListActivity.class);
            intent.putExtra("NAME", child.get(KEY_NAME));
        }

        intent.putExtra("TYPE", type.toString());

        startActivity(intent);

        return true;
    }


    private void forceSync() {
        startSyncAnimation();

        _disposables.add(
            Flowable.fromCallable(() -> _getYearsTask.call())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    this::onSyncComplete,
                    ex -> {
                        stopSyncAnimation();
                        _authHandler.handleException(ex);
                })
        );
    }


    private void onSyncComplete(List<Integer> years) {
        // go back to the database rather than inspecting results, as there poller may have happened
        // before the sync, which means that the new year would not be in the list of results
        prepareYearChildren(years);

        _adapter.notifyDataSetChanged();

        stopSyncAnimation();
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
                _yearChildren.add(createChild(year.toString(), PhotoListType.ByCategory));
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

        children.add(createChild("Newest", PhotoListType.ByCommentsNewest));
        children.add(createChild("Oldest", PhotoListType.ByCommentsOldest));
        children.add(createChild("Your Newest", PhotoListType.ByUserCommentsNewest));
        children.add(createChild("Your Oldest", PhotoListType.ByUserCommentsOldest));
        children.add(createChild("Most Comments", PhotoListType.ByCommentCountMost));
        children.add(createChild("Least Comments", PhotoListType.ByCommentCountLeast));

        // by ratings
        children = new ArrayList<>();
        _groupData.add(createGroup("By Rating"));
        _childData.add(children);

        children.add(createChild("Average Rating", PhotoListType.ByAverageRating));
        children.add(createChild("Your Rating", PhotoListType.ByUserRating));

        // random
        children = new ArrayList<>();
        _groupData.add(createGroup("Random"));
        _childData.add(children);

        children.add(createChild("Surprise Me!", PhotoListType.Random));

        _modeExpandableListView.setAdapter(_adapter);
    }


    private Map<String, String> createGroup(String name) {
        Map<String, String> group = new HashMap<>();
        group.put(KEY_NAME, name);

        return group;
    }


    private Map<String, String> createChild(String name, PhotoListType type) {
        Map<String, String> child = new HashMap<>();
        child.put(KEY_NAME, name);
        child.put(KEY_TYPE, type.toString());

        return child;
    }


    private void startSyncAnimation() {
        ImageView iv = (ImageView) getLayoutInflater().inflate(R.layout.refresh_indicator, _toolbar, false);

        Animation rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate);
        rotation.setRepeatCount(Animation.INFINITE);
        iv.startAnimation(rotation);

        _refreshMenuItem.setActionView(iv);
    }


    private void stopSyncAnimation() {
        View v = _refreshMenuItem.getActionView();

        if(v != null) {
            v.clearAnimation();
            _refreshMenuItem.setActionView(null);
        }
    }
}
