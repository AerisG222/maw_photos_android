package us.mikeandwan.photos.ui.mode

import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.SimpleExpandableListAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import us.mikeandwan.photos.MawApplication
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityModeSelectionBinding
import us.mikeandwan.photos.models.ApiCollection
import us.mikeandwan.photos.models.Category
import us.mikeandwan.photos.services.AuthStateManager
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.services.PhotoListType
import us.mikeandwan.photos.services.UpdateCategoriesJobScheduler
import us.mikeandwan.photos.ui.BaseActivity
import us.mikeandwan.photos.ui.categories.CategoryListActivity
import us.mikeandwan.photos.ui.login.LoginActivity
import us.mikeandwan.photos.ui.photos.PhotoListActivity
import us.mikeandwan.photos.ui.receiver.PhotoReceiverActivity
import us.mikeandwan.photos.ui.settings.SettingsActivity
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ModeSelectionActivity : BaseActivity() {
    private val _disposables = CompositeDisposable()
    private val _groupData: MutableList<Map<String, String?>> = ArrayList()
    private val _childData: MutableList<List<Map<String, String?>>> = ArrayList()
    private val _yearChildren: MutableList<Map<String, String?>> = ArrayList()
    private var _adapter: SimpleExpandableListAdapter? = null
    private var _yearList: List<Int>? = null
    private var _refreshMenuItem: MenuItem? = null
    private var _app: MawApplication? = null

    private lateinit var binding: ActivityModeSelectionBinding

    @Inject lateinit var _dataServices: DataServices
    @Inject lateinit  var _authStateManager: AuthStateManager
    @Inject lateinit var _updateScheduler: UpdateCategoriesJobScheduler
    @Inject lateinit var _sharedPrefs: SharedPreferences

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModeSelectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _app = application as MawApplication
        updateToolbar(binding.toolbar, null)
        binding.modeExpandableListView.setOnChildClickListener { parent: ExpandableListView, v: View, groupPosition: Int, childPosition: Int, id: Long ->
            onItemClicked(
                parent,
                v,
                groupPosition,
                childPosition,
                id
            )
        }
        _adapter = SimpleExpandableListAdapter(
            this,
            _groupData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf(KEY_NAME),
            intArrayOf(android.R.id.text1),
            _childData,
            android.R.layout.simple_expandable_list_item_1,
            arrayOf(KEY_NAME),
            intArrayOf(android.R.id.text1)
        )
        _updateScheduler.schedule(false, FOUR_HOURS_IN_MILLIS)
        initModeList()
        resetNotifications()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.mode_selection, menu)
        _refreshMenuItem = menu.findItem(R.id.action_force_sync)
        return true
    }

    public override fun onRestart() {
        // we do this on restart, so that if a new year was pulled during a refresh of the category
        // screen, or poller, this page will show the latest years we have available automatically
        prepareYearChildren()
        _adapter!!.notifyDataSetChanged()
        super.onRestart()
    }

    override fun onDestroy() {
        super.onDestroy()
        _disposables.clear() // do not send event after activity has been destroyed
    }

    fun onMenuItemSettings(menuItem: MenuItem?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun onMenuItemForceSync(menuItem: MenuItem?) {
        forceSync()
    }

    fun onViewUploadQueueClick(menuItem: MenuItem?) {
        val intent = Intent(this, PhotoReceiverActivity::class.java)
        startActivity(intent)
    }

    fun onWipeCache(menuItem: MenuItem?) {
        _disposables.add(
            Flowable.fromCallable {
                _dataServices.wipeCache()
                true
            }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { b: Boolean -> onWipeComplete(b) }
                ) { ex: Throwable? ->
                    stopSyncAnimation()
                    handleApiException(ex)
                }
        )
    }

    fun onReauthenticate(menuItem: MenuItem?) {
        // TODO: check the line below
        //_authStateManager!!.replace(null)

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun resetNotifications() {
        val mgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mgr.cancel(0)
        _app!!.notificationCount = 0
    }

    private fun onWipeComplete(b: Boolean) {
        Snackbar.make(binding.toolbar, "Wipe complete.", Snackbar.LENGTH_SHORT).show()
    }

    private fun onItemClicked(
        parent: ExpandableListView,
        v: View,
        groupPosition: Int,
        childPosition: Int,
        id: Long
    ): Boolean {
        val intent: Intent
        val child = _childData[groupPosition][childPosition]
        val type = PhotoListType.valueOf(child[KEY_TYPE]!!)
        if (type == PhotoListType.ByCategory) {
            intent = Intent(parent.context, CategoryListActivity::class.java)
            intent.putExtra("YEAR", child[KEY_NAME]!!.toInt())
        } else {
            intent = Intent(parent.context, PhotoListActivity::class.java)
            intent.putExtra("NAME", child[KEY_NAME])
        }
        intent.putExtra("TYPE", type.toString())
        startActivity(intent)
        return true
    }

    private fun forceSync() {
        startSyncAnimation()
        _disposables.add(
            Flowable.fromCallable { _dataServices.recentCategories }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { categories: ApiCollection<Category> -> onSyncComplete(categories) }
                ) { ex: Throwable? ->
                    stopSyncAnimation()
                    handleApiException(ex)
                }
        )
    }

    private fun onSyncComplete(categories: ApiCollection<Category>) {
        stopSyncAnimation()
        val count = categories.count
        if (count == 0L) {
            Snackbar.make(binding.modeExpandableListView, "No updates available.", Snackbar.LENGTH_SHORT)
                .show()
            return
        }

        // TODO: simplify if/when we update api
        /*
        categories
                .stream()
                .filter(x -> !_yearList.contains(x.getYear()))
                .collect(Collectors.toList());
        */
        val newYears: MutableList<Int> = ArrayList()
        for (cat in categories.items) {
            if (!_yearList!!.contains(cat.year)) {
                newYears.add(cat.year)
            }
        }
        var year = "year"
        var cat = "category"
        if (count != 1L) {
            cat = "categories"
        }
        if (newYears.size != 1) {
            year = "years"
        }
        Snackbar.make(
            binding.modeExpandableListView,
            count.toString() + " new " + cat + " found. " + newYears.size + " new " + year + " added.",
            Snackbar.LENGTH_SHORT
        ).show()
        if (newYears.size > 0) {
            prepareYearChildren()
            _adapter!!.notifyDataSetChanged()
        }
    }

    private fun prepareYearChildren() {
        val years = _dataServices.photoYears ?: return

        if (_yearList == null || years.size != _yearList!!.size) {
            _yearList = years
            _yearChildren.clear()
            for (year in _yearList!!) {
                _yearChildren.add(createChild(year.toString(), PhotoListType.ByCategory))
            }
        }
    }

    private fun initModeList() {
        val children: MutableList<Map<String, String?>>

        // by year
        prepareYearChildren()
        _groupData.add(createGroup("Year"))
        _childData.add(_yearChildren)

        // random
        children = ArrayList()
        _groupData.add(createGroup("Random"))
        _childData.add(children)
        children.add(createChild("Surprise Me!", PhotoListType.Random))
        binding.modeExpandableListView.setAdapter(_adapter)
    }

    private fun createGroup(name: String): Map<String, String?> {
        val group: MutableMap<String, String?> = HashMap()
        group[KEY_NAME] = name
        return group
    }

    private fun createChild(name: String, type: PhotoListType): Map<String, String?> {
        val child: MutableMap<String, String?> = HashMap()
        child[KEY_NAME] = name
        child[KEY_TYPE] = type.toString()
        return child
    }

    private fun startSyncAnimation() {
        val iv = layoutInflater.inflate(R.layout.refresh_indicator, binding.toolbar, false) as ImageView
        val rotation = AnimationUtils.loadAnimation(this, R.anim.refresh_rotate)
        rotation.repeatCount = Animation.INFINITE
        iv.startAnimation(rotation)
        _refreshMenuItem!!.actionView = iv
    }

    private fun stopSyncAnimation() {
        val v = _refreshMenuItem!!.actionView
        if (v != null) {
            v.clearAnimation()
            _refreshMenuItem!!.actionView = null
        }
    }

    companion object {
        private const val KEY_NAME = "NAME"
        private const val KEY_TYPE = "TYPE"
        private const val FOUR_HOURS_IN_MILLIS = (4 * 60 * 60 * 1000 // 4 hours
                ).toLong()
    }
}