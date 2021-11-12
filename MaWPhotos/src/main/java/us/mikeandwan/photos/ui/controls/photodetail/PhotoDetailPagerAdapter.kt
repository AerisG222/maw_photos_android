package us.mikeandwan.photos.ui.controls.photodetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import us.mikeandwan.photos.ui.controls.photocomment.PhotoCommentFragment
import us.mikeandwan.photos.ui.controls.photodetail.PhotoDetailBottomSheetFragment.Companion.TAB_INDEX_COMMENTS
import us.mikeandwan.photos.ui.controls.photodetail.PhotoDetailBottomSheetFragment.Companion.TAB_INDEX_EXIF
import us.mikeandwan.photos.ui.controls.photodetail.PhotoDetailBottomSheetFragment.Companion.TAB_INDEX_RATING
import us.mikeandwan.photos.ui.controls.photoexif.PhotoExifFragment
import us.mikeandwan.photos.ui.controls.photorating.PhotoRatingFragment

@ExperimentalCoroutinesApi
class PhotoDetailPagerAdapter(fragment: Fragment)
    : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when(position) {
            TAB_INDEX_COMMENTS -> PhotoCommentFragment()
            TAB_INDEX_EXIF -> PhotoExifFragment()
            TAB_INDEX_RATING -> PhotoRatingFragment()
            else -> throw IllegalArgumentException("position unknown: $position")
        }
    }
}