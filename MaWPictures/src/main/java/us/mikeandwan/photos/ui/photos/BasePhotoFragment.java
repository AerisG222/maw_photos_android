package us.mikeandwan.photos.ui.photos;

import android.content.Context;

import us.mikeandwan.photos.ui.BaseFragment;


public class BasePhotoFragment extends BaseFragment {
    public Context getContext() {
        return getActivity().getBaseContext();
    }


    IPhotoActivity getPhotoActivity() {
        return (IPhotoActivity) getActivity();
    }


    void updateProgress() {
        getPhotoActivity().updateProgress();
    }
}
