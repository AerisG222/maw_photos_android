package us.mikeandwan.photos.uinew.ui.photo

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.flow.StateFlow
import us.mikeandwan.photos.domain.Photo

class PhotoFragmentStateAdapter(private val photos: StateFlow<List<Photo>>, fragment: Fragment): FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return photos.value.size
    }

    override fun createFragment(position: Int): Fragment {
        val fragment = PhotoViewHolderFragment()

        fragment.arguments = Bundle().apply {
            val photo = photos.value[position]

            putString(PhotoViewHolderFragment.PHOTO_URL, photo.mdUrl)
        }

        return fragment
    }
}