package us.mikeandwan.photos.uinew.ui.photodetail

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import us.mikeandwan.photos.uinew.ui.photocomment.PhotoCommentFragment
import us.mikeandwan.photos.uinew.ui.photodetail.PhotoDetailBottomSheetFragment.Companion.TAB_INDEX_COMMENTS
import us.mikeandwan.photos.uinew.ui.photodetail.PhotoDetailBottomSheetFragment.Companion.TAB_INDEX_EXIF
import us.mikeandwan.photos.uinew.ui.photodetail.PhotoDetailBottomSheetFragment.Companion.TAB_INDEX_RATING
import us.mikeandwan.photos.uinew.ui.photoexif.PhotoExifFragment
import us.mikeandwan.photos.uinew.ui.photorating.PhotoRatingFragment

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