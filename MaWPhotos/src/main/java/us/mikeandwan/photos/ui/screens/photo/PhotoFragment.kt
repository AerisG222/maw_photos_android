package us.mikeandwan.photos.ui.screens.photo

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.github.chrisbanes.photoview.PhotoView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import us.mikeandwan.photos.databinding.FragmentPhotoBinding
import us.mikeandwan.photos.domain.Photo
import us.mikeandwan.photos.ui.controls.photodetail.PhotoDetailBottomSheetFragment
import us.mikeandwan.photos.utils.GlideApp
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.net.URL
import java.util.*

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class PhotoFragment : Fragment() {
    companion object {
        fun newInstance() = PhotoFragment()

        const val TAG = "ModalBottomSheet"
    }

    private lateinit var binding: FragmentPhotoBinding
    val viewModel by viewModels<PhotoViewModel>()

    private val pageChangeCallback = object: ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            viewModel.updateActivePhoto(position)
            super.onPageSelected(position)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.pager.adapter = PhotoFragmentStateAdapter(this)
        binding.pager.registerOnPageChangeCallback(pageChangeCallback)

        binding.info.setOnClickListener {
            val modalBottomSheet = PhotoDetailBottomSheetFragment()
            modalBottomSheet.show(childFragmentManager, TAG)
        }

        initStateObservers()

        return binding.root
    }

    override fun onDestroy() {
        binding.pager.unregisterOnPageChangeCallback(pageChangeCallback)

        super.onDestroy()
    }

    private fun rotatePhoto(direction: Int) {
        val photoView = binding.pager.findViewWithTag<PhotoView>(PhotoViewHolderFragment.TAG_PHOTO_VIEW)

        photoView.setRotationBy(90f * direction)

        viewModel.rotateComplete()
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.photos
                    .filter { it.isNotEmpty() }
                    .combine(viewModel.activePhotoIndex) { photos, index -> Pair(photos, index) }
                    .filter { it.second >= 0 }
                    .onEach { (photos, index) ->
                        if(photos.size != binding.pager.adapter?.itemCount) {
                            Timber.i("updating adapter, new count: ${photos.size} old count: ${binding.pager.adapter?.itemCount}")
                            (binding.pager.adapter as PhotoFragmentStateAdapter).updatePhotoList(photos)
                        }

                        Timber.i("setting item to: $index")
                        binding.pager.setCurrentItem(index, true)
                        binding.positionTextView.text = "${index + 1} / ${photos.size}"
                    }
                    .launchIn(this)

                viewModel.rotatePhoto
                    .filter { it != 0 }
                    .onEach {
                        delay(1)
                        rotatePhoto(it)
                    }
                    .launchIn(this)

                viewModel.sharePhoto
                    .filter { it != null }
                    .onEach { sharePhoto(it!!) }
                    .launchIn(this)
            }
        }
    }

    private suspend fun sharePhoto(photo: Photo) = coroutineScope {
        viewModel.sharePhotoComplete()

        val fileToShare = getTempFileName(photo)

        saveTempFile(photo, fileToShare)

        val contentUri = FileProvider.getUriForFile(requireActivity(), "us.mikeandwan.photos.fileprovider", fileToShare)
        val sendIntent = Intent(Intent.ACTION_SEND)

        sendIntent.setDataAndType(contentUri, "image/*")
        sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        sendIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

        val shareIntent = Intent.createChooser(sendIntent, null)

        startActivity(shareIntent)
    }

    private suspend fun saveTempFile(photo: Photo, fileToShare: File) = coroutineScope {
        launch(Dispatchers.IO) {
            // reuse glide to try to pull the cached image
            val drawable = GlideApp.with(requireActivity())
                .load(photo.mdUrl)
                .submit()
                .get()

            val outputStream = FileOutputStream(fileToShare)
            val bitmap = (drawable as BitmapDrawable).bitmap

            if (!bitmap.compress(Bitmap.CompressFormat.JPEG, 92, outputStream)) {
                throw Exception("failed to save drawable!")
            }
        }
    }

    private fun getTempFileName(photo: Photo): File {
        val extension = URL(photo.mdUrl).file.substringAfterLast('.')
        val rootPath = requireActivity().getExternalFilesDir("photos")

        return File(rootPath, "${UUID.randomUUID()}.${extension}")
    }
}