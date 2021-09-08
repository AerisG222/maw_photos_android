package us.mikeandwan.photos.ui.initialLoad

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import us.mikeandwan.photos.databinding.ActivityInitialLoadBinding
import us.mikeandwan.photos.models.ApiCollection
import us.mikeandwan.photos.models.Category
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.ui.BaseActivity
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity
import javax.inject.Inject

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