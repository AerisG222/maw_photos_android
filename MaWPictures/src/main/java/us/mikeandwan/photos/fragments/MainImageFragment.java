package us.mikeandwan.photos.fragments;


import android.support.v4.view.ViewPager;

import com.example.touch.FullScreenImageAdapter;
import com.example.touch.TouchViewPager;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import us.mikeandwan.photos.R;


@SuppressWarnings("ALL")
@EFragment(R.layout.fragment_main_image)
public class MainImageFragment extends BasePhotoFragment {
    private FullScreenImageAdapter _adapter;

    @ViewById(R.id.pager)
    protected TouchViewPager _pager;


    @AfterViews
    protected void afterViews() {
        _adapter = new FullScreenImageAdapter(getContext(), getPhotoActivity());

        _pager.setPageMargin(20);
        _pager.setAdapter(_adapter);

        _pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                // do nothing
            }

            @Override
            public void onPageSelected(int position) {
                getPhotoActivity().gotoPhoto(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                // do nothing
            }
        });
    }


    public void onPhotoListUpdated() {
        _adapter.notifyDataSetChanged();
    }


    public void onCurrentPhotoUpdated() {
        int index = getPhotoActivity().getCurrentIndex();

        if (index != _pager.getCurrentItem()) {
            _pager.setCurrentItem(index);
        }
    }


    public void rotatePhoto(int direction) {
        _pager.rotateImage(direction);
    }
}