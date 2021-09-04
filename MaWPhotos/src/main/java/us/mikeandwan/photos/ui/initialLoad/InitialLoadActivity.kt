package us.mikeandwan.photos.ui.initialLoad

import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.ui.BaseActivity
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject
import us.mikeandwan.photos.services.DataServices
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Flowable
import us.mikeandwan.photos.models.ApiCollection
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import timber.log.Timber
import android.content.Intent
import us.mikeandwan.photos.databinding.ActivityInitialLoadBinding
import us.mikeandwan.photos.models.Category
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity

@AndroidEntryPoint
class InitialLoadActivity : BaseActivity() {
    private val _disposables = CompositeDisposable()
    private lateinit var binding: ActivityInitialLoadBinding

    @JvmField
    @Inject
    var _dataServices: DataServices? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialLoadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        completeLoginProcess()
    }

    override fun onDestroy() {
        super.onDestroy()
        _disposables.clear() // do not send event after activity has been destroyed
    }

    private fun completeLoginProcess() {
        Snackbar.make(binding.initialLoadLayout, "Getting things ready...", Snackbar.LENGTH_SHORT).show()
        _disposables.add(
            Flowable.fromCallable { _dataServices!!.recentCategories }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { x: ApiCollection<Category>? -> goToModeSelection() }
                ) { ex: Throwable ->
                    Timber.e("error loading categories: %s", ex.message)
                    handleApiException(ex)
                    goToModeSelection()
                }
        )
    }

    private fun goToModeSelection() {
        val intent = Intent(this, ModeSelectionActivity::class.java)
        startActivity(intent)
        finish()
    }
}