package us.mikeandwan.photos.fragments;

import android.app.Fragment;
import android.content.Context;

import us.mikeandwan.photos.activities.IPhotoActivity;
import us.mikeandwan.photos.data.Photo;


public class BasePhotoFragment extends Fragment {
    protected Context getContext() {
        return getActivity().getBaseContext();
    }


    protected IPhotoActivity getPhotoActivity() {
        return (IPhotoActivity) getActivity();
    }


    protected void updateProgress() {
        getPhotoActivity().updateProgress();
    }


    protected Photo getCurrentPhoto() {
        return getPhotoActivity().getCurrentPhoto();
    }
}
