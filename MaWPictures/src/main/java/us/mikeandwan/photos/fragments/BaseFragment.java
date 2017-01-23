package us.mikeandwan.photos.fragments;

import android.app.Fragment;

import us.mikeandwan.photos.activities.HasComponent;


public class BaseFragment extends Fragment {
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>)getActivity()).getComponent());
    }
}
