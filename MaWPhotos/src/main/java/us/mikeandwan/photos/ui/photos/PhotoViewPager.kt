package us.mikeandwan.photos.ui.photos

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager
import com.github.chrisbanes.photoview.PhotoView
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
// https://raw.githubusercontent.com/chrisbanes/PhotoView/master/sample/src/main/java/uk/co/senab/photoview/sample/HackyViewPager.java
class PhotoViewPager(context: Context?, attrs: AttributeSet?) : ViewPager(
    context!!, attrs
) {
    private val _photoSelectedSubject = PublishSubject.create<Int>()
    private var _enabled = true
    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return try {
            _enabled && super.onInterceptTouchEvent(arg0)
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
            false
        }
    }

    fun onPhotoSelected(): Observable<Int> {
        return _photoSelectedSubject.hide()
    }

    override fun isEnabled(): Boolean {
        return _enabled
    }

    override fun setEnabled(enabled: Boolean) {
        _enabled = enabled
    }

    fun rotateImage(direction: Int) {
        val pv: PhotoView = findViewWithTag(currentItem)
        pv.rotation = pv.rotation + direction * 90
    }

    init {
        addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                // do nothing
            }

            override fun onPageSelected(position: Int) {
                _photoSelectedSubject.onNext(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
                // do nothing
            }
        })
    }
}