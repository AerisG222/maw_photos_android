package us.mikeandwan.photos.fragments;

import android.app.Fragment;
import android.content.Context;

import us.mikeandwan.photos.activities.IPhotoActivity;


public class BasePhotoFragment extends Fragment {
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
