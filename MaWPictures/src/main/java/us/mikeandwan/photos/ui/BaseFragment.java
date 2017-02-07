package us.mikeandwan.photos.ui;

import android.app.Fragment;

import us.mikeandwan.photos.ui.HasComponent;


public class BaseFragment extends Fragment {
    protected <C> C getComponent(Class<C> componentType) {
        return componentType.cast(((HasComponent<C>)getActivity()).getComponent());
    }
}