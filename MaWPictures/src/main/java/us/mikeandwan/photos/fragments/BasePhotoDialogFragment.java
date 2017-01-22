package us.mikeandwan.photos.fragments;

import android.app.DialogFragment;
import android.content.Context;

import us.mikeandwan.photos.activities.IPhotoActivity;
import us.mikeandwan.photos.models.Photo;


public class BasePhotoDialogFragment extends DialogFragment {
    public Context getContext() {
        return getActivity().getBaseContext();
    }


    private IPhotoActivity getPhotoActivity() {
        return (IPhotoActivity) getActivity();
    }


    void updateProgress() {
        getPhotoActivity().updateProgress();
    }


    Photo getCurrentPhoto() {
        return getPhotoActivity().getCurrentPhoto();
    }
}
