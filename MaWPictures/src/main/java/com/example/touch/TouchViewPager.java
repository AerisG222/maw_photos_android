package com.example.touch;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import us.mikeandwan.photos.R;


// http://stackoverflow.com/questions/11306037/how-to-implement-zoom-pan-and-drag-on-viewpager-in-android
@SuppressWarnings("ALL")
public class TouchViewPager extends ViewPager {
    private boolean _enabled = true;


    public TouchViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return _enabled && super.onInterceptTouchEvent(arg0);
    }


    public boolean isEnabled() {
        return _enabled;
    }


    public void setEnabled(boolean enabled) {
        _enabled = enabled;
    }


    public void rotateImage(int direction) {
        View v = findViewWithTag(getCurrentItem());
        TouchImageView tiv = (TouchImageView) v.findViewById(R.id.imgDisplay);

        tiv.rotateImage(direction);
    }
}