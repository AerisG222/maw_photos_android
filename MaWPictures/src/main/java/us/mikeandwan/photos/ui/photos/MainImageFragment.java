package us.mikeandwan.photos.ui.photos;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.services.PhotoApiClient;
import us.mikeandwan.photos.services.PhotoStorage;


public class MainImageFragment extends BasePhotoFragment {
    private Unbinder _unbinder;

    @BindView(R.id.pager) PhotoViewPager _pager;

    @Inject PhotoStorage _photoStorage;
    @Inject PhotoApiClient _photoClient;
    @Inject FullScreenImageAdapter _adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_image, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(TaskComponent.class).inject(this);

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


    @Override
    public void onDestroy() {
        super.onDestroy();
        _unbinder.unbind();
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