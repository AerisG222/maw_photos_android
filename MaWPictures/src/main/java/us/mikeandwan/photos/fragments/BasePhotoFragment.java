package us.mikeandwan.photos.fragments;

import android.content.Context;

import us.mikeandwan.photos.activities.IPhotoActivity;


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
