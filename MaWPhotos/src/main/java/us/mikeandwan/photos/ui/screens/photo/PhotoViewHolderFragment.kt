package us.mikeandwan.photos.ui.screens.photo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import us.mikeandwan.photos.R

class PhotoViewHolderFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.view_holder_photo, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.takeIf {
            it.containsKey(PHOTO_URL) }?.apply {
                val photoView = view.findViewById(R.id.photoView) as PhotoView

                Glide.with(photoView.context)
                    .load(getString(PHOTO_URL))
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_broken_image)
                    .into(photoView)
            }
    }

    companion object {
        const val PHOTO_URL = "photo_url"
        const val TAG_PHOTO_VIEW = "photoview"
    }
}