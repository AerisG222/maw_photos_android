package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import com.github.chrisbanes.photoview.PhotoView;


// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
// https://raw.githubusercontent.com/chrisbanes/PhotoView/master/sample/src/main/java/uk/co/senab/photoview/sample/HackyViewPager.java
public class PhotoViewPager extends ViewPager {
    private final PublishSubject<Integer> _photoSelectedSubject = PublishSubject.create();
    private boolean _enabled = true;


    public PhotoViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);

        addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                _photoSelectedSubject.onNext(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }
        });
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        try {
            return _enabled && super.onInterceptTouchEvent(arg0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return false;
        }
    }


    public Observable<Integer> onPhotoSelected() {
        return _photoSelectedSubject.hide();
    }


    public boolean isEnabled() {
        return _enabled;
    }


    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }


    public void rotateImage(int direction) {
        PhotoView pv = (PhotoView) findViewWithTag(getCurrentItem());

        pv.setRotation(pv.getRotation() + (direction * 90));
    }
}