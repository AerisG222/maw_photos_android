package us.mikeandwan.photos.ui.categories

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityCategoryListBinding
import us.mikeandwan.photos.models.ApiCollection
import us.mikeandwan.photos.models.Category
import us.mikeandwan.photos.prefs.CategoryDisplay
import us.mikeandwan.photos.prefs.CategoryDisplayPreference
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.services.PhotoListType
import us.mikeandwan.photos.ui.BaseActivity
import us.mikeandwan.photos.ui.photos.PhotoListActivity
import us.mikeandwan.photos.ui.receiver.PhotoReceiverActivity
import us.mikeandwan.photos.ui.settings.SettingsActivity
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class CategoryListActivity : BaseActivity(), ICategoryListActivity {
    private val _disposables = CompositeDisposable()
    private var _year = 0
    private var _categories: MutableList<Category>? = null
    private var _refreshMenuItem: MenuItem? = null
    private var _listener: OnGlobalLayoutListener? = null
    private var _decoration: DividerItemDecoration? = null

    private lateinit var binding: ActivityCategoryListBinding

    var _thumbSize: Int = 0

    @Inject lateinit var _dataServices: DataServices
    @Inject lateinit var _categoryPrefs: CategoryDisplayPreference
    @Inject lateinit var _listAdapter: ListCategoryRecyclerAdapter
    @Inject lateinit var _gridAdapter: ThumbnailCategoryRecyclerAdapter

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _thumbSize = resources.getDimension(R.dimen.category_grid_thumbnail_size).toInt()

        _year = intent.getIntExtra("YEAR", 0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.category_list, menu)
        _refreshMenuItem = menu.findItem(R.id.action_force_sync)
        return true
    }

    public override fun onResume() {
        updateToolbar(binding.toolbar, _year.toString())
        binding.categoryRecyclerView.setHasFixedSize(true)
        if (_categoryPrefs.categoryDisplay == CategoryDisplay.Grid) {
            // http://stackoverflow.com/questions/25396747/how-to-get-fragment-width
            _listener = OnGlobalLayoutListener {
                binding.categoryRecyclerView.post {
                    val width = binding.container.width
                    if (width > 0) {
                        binding.container.viewTreeObserver.removeOnGlobalLayoutListener(_listener)
                        updateGridSize(width)
                    }
                }
            }
            binding.container.viewTreeObserver.addOnGlobalLayoutListener(_listener)
        }

        val categories = _dataServices.getCategoriesForYear(_year)

        if(categories != null) {
            setCategories(categories.toMutableList())
        }

        super.onResume()
    }

    override fun onDestroy() {
        _disposables.clear() // do not send event after activity has been destroyed
        _gridAdapter.dispose()
        _listAdapter.dispose()
        super.onDestroy()
    }

    override fun onApiException(throwable: Throwable?) {
        handleApiException(throwable)
    }

    fun onMenuSettingsClick(menuItem: MenuItem?) {
        val intent = Intent(this, SettingsActivity::class.java)
        startActivity(intent)
    }

    fun onMenuSyncClick(menuItem: MenuItem?) {
        forceSync()
    }

    fun onViewUploadQueueClick(menuItem: MenuItem?) {
        val intent = Intent(this, PhotoReceiverActivity::class.java)
        startActivity(intent)
    }

    override fun selectCategory(category: Category?) {
        val intent = Intent(this, PhotoListActivity::class.java)
        intent.putExtra("NAME", category!!.name)
        intent.putExtra("TYPE", PhotoListType.ByCategory.toString())
        intent.putExtra("CATEGORY_ID", category.id)
        startActivity(intent)
    }

    private fun updateGridSize(width: Int) {
        val cols = Math.max(1, width / _thumbSize)
        val glm = GridLayoutManager(this, cols)
        binding.categoryRecyclerView.layoutManager = glm
        binding.categoryRecyclerView.recycledViewPool.clear()
    }

    private fun setCategories(categories: MutableList<Category>) {
        _categories = categories

        if (_categoryPrefs.categoryDisplay == CategoryDisplay.Grid) {
            _gridAdapter.setCategoryList(categories)
            _disposables.add(
                _gridAdapter.onCategorySelected()
                    .subscribe { category: Category -> selectCategory(category) })
            if (_decoration != null) {
                binding.categoryRecyclerView.removeItemDecoration(_decoration!!)
                _decoration = null
            }
            binding.categoryRecyclerView.adapter = _gridAdapter
        } else {
            _listAdapter.setCategoryList(categories)
            _disposables.add(
                _listAdapter.onCategorySelected()
                    .subscribe { category: Category -> selectCategory(category) })
            val llm = LinearLayoutManager(this)
            llm.orientation = RecyclerView.VERTICAL
            binding.categoryRecyclerView.layoutManager = llm
            if (_decoration == null) {
                _decoration =
                    DividerItemDecoration(binding.categoryRecyclerView.context, llm.orientation)
                binding.categoryRecyclerView.addItemDecoration(_decoration!!)
            }
            binding.categoryRecyclerView.adapter = _listAdapter
        }
    }

    private fun forceSync() {
        startSyncAnimation()
        _disposables.add(
            Flowable.fromCallable { _dataServices.recentCategories }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result: ApiCollection<Category> -> onSyncComplete(result) }) { ex: Throwable ->
                    onException(
                        ex
                    )
                }
        )
    }

    private fun onException(ex: Throwable) {
        stopSyncAnimation()
        handleApiException(ex)
    }

    private fun notifyCategoriesUpdated() {
        if (_categoryPrefs.categoryDisplay == CategoryDisplay.Grid) {
            _gridAdapter.notifyDataSetChanged()
        } else {
            _listAdapter.notifyDataSetChanged()
        }
    }

    private fun onSyncComplete(result: ApiCollection<Category>) {
        stopSyncAnimation()
        val count = result.count
        if (count == 0L) {
            Snackbar.make(binding.container, "No updates available.", Snackbar.LENGTH_SHORT).show()
            return
        }

        // TODO: use streams if/when we update api
        /*
        result
            .stream()
            .filter(x -> x.getYear() == _year)
            .collect(Collectors.toList());
        */
        val newForYear: MutableList<Category> = ArrayList()
        for (cat in result.items) {
            if (cat.year == _year) {
                newForYear.add(cat)
            }
        }
        var cat = "category"
        if (count != 1L) {
            cat = "categories"
        }
        Snackbar.make(
            binding.container,
            count.toString() + " new " + cat + " found. " + newForYear.size + " added for " + _year + ".",
            Snackbar.LENGTH_SHORT
        ).show()
        if (newForYear.size > 0) {
            _categories!!.addAll(newForYear)
            notifyCategoriesUpdated()
        }
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
}