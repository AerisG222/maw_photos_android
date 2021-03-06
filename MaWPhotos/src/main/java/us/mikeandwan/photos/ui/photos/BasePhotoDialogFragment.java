package us.mikeandwan.photos.ui.photos;

import android.app.DialogFragment;
import android.content.Context;

import us.mikeandwan.photos.models.Photo;
import us.mikeandwan.photos.ui.HasComponent;


public class BasePhotoDialogFragment extends DialogFragment {
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>)getActivity()).getComponent());
    }


    public Context getContext() {
        return getActivity().getBaseContext();
    }


    protected IPhotoActivity getPhotoActivity() {
        return (IPhotoActivity) getActivity();
    }


    void addWork() {
        getPhotoActivity().addWork();
    }


    void removeWork() { getPhotoActivity().removeWork(); }


    Photo getCurrentPhoto() {
        return getPhotoActivity().getCurrentPhoto();
    }
}
