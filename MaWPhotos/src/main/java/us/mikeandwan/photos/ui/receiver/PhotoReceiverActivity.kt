package us.mikeandwan.photos.ui.receiver

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.ActivityPhotoReceiverBinding
import us.mikeandwan.photos.services.DataServices
import us.mikeandwan.photos.services.UploadJobScheduler
import us.mikeandwan.photos.ui.BaseActivity
import java.io.File
import java.io.FileNotFoundException
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class PhotoReceiverActivity : BaseActivity() {
    private val _disposables = CompositeDisposable()
    private var _thumbSize = 0

    @Inject lateinit var _dataServices: DataServices
    @Inject lateinit var _receiverAdapter: ReceiverRecyclerAdapter
    @Inject lateinit var _uploadScheduler: UploadJobScheduler

    private lateinit var binding: ActivityPhotoReceiverBinding

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoReceiverBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _thumbSize = resources.getDimension(R.dimen.image_grid_thumbnail_size_medium).toInt()
        binding.receiverRecyclerView.setHasFixedSize(true)
        binding.receiverRecyclerView.adapter = _receiverAdapter

        setLayoutManager()

        val intent = intent
        val action = intent.action

        if (action != null) {
            when (action) {
                Intent.ACTION_SEND -> handleSendSingle(intent)
                Intent.ACTION_SEND_MULTIPLE -> handleSendMultiple(intent)
            }
        }

        // if we end up on this page, we either have new files to upload, or a user wants to check
        // so lets try to reschedule the job to kick it off
        _uploadScheduler.schedule(true)
    }

    public override fun onResume() {
        updateToolbar(binding.toolbar, "Upload Queue")

        _disposables.add(_dataServices
            .fileQueueObservable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { files: Array<File>? -> updateListing(files!!) })

        super.onResume()
    }

    override fun onDestroy() {
        _disposables.clear()
        super.onDestroy()
    }

    private fun setLayoutManager() {
        // https://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
        val displayMetrics = resources.displayMetrics
        val cols = displayMetrics.widthPixels / _thumbSize
        val glm = GridLayoutManager(this, Math.max(1, cols))
        binding.receiverRecyclerView.layoutManager = glm
        binding.receiverRecyclerView.itemAnimator = DefaultItemAnimator()
        _receiverAdapter.setItemSize(displayMetrics.widthPixels / cols)
        binding.receiverRecyclerView.recycledViewPool.clear()
    }

    private fun isValidType(type: String?): Boolean {
        return type != null && (type.startsWith("image/") || type.startsWith("video/"))
    }

    fun handleSendSingle(intent: Intent) {
        val mediaUri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        val list = ArrayList<Uri?>()
        list.add(mediaUri)
        saveFiles(list)
    }

    fun handleSendMultiple(intent: Intent) {
        val mediaUris = intent.getParcelableArrayListExtra<Uri?>(Intent.EXTRA_STREAM)
        saveFiles(mediaUris)
    }

    fun saveFiles(mediaUris: ArrayList<Uri?>?) {
        _disposables.add(
            Flowable.fromCallable { enqueueFiles(mediaUris) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { msg: String? -> Snackbar.make(binding.photoReceiverLayout, msg!!, Snackbar.LENGTH_LONG).show() }
                ) { ex: Throwable ->
                    Timber.e("error loading categories: %s", ex.message)
                    handleApiException(ex)
                })
    }

    @Throws(FileNotFoundException::class)
    private fun enqueueFiles(mediaUris: ArrayList<Uri?>?): String {
        var count = 0
        var unsupportedFiles = 0
        for (uri in mediaUris!!) {
            val type = contentResolver.getType(uri!!)
            if (!isValidType(type)) {
                unsupportedFiles++
                continue
            }
            val inputStream = contentResolver.openInputStream(uri)
            if (_dataServices.enqueueFileToUpload(count + 1, inputStream!!, type!!)) {
                count++
            }
        }
        var msg = ""
        if (count > 0) {
            msg += "Successfully enqueued $count files for upload."
        }
        if (unsupportedFiles > 0) {
            if (msg.length > 0) {
                msg += "  "
            }
            msg += "Unable to enqueue $unsupportedFiles files."
        }
        return msg
    }

    private fun updateListing(files: Array<File>) {
        _receiverAdapter.setQueuedFiles(files)
    }
}