package us.mikeandwan.photos.ui.screens.photo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import us.mikeandwan.photos.domain.models.Photo

class PhotoFragmentStateAdapter(fragment: Fragment): FragmentStateAdapter(fragment) {
    private var _photos = emptyList<Photo>()

    override fun getItemCount(): Int {
        return _photos.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = PhotoViewHolderFragment()

        fragment.arguments = Bundle().apply {
            val photo = _photos[position]

            putString(PhotoViewHolderFragment.PHOTO_URL, photo.mdUrl)
        }

        return fragment
    }

    fun updatePhotoList(photos: List<Photo>) {
        _photos = photos
        notifyDataSetChanged()
    }
}