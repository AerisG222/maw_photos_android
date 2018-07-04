package us.mikeandwan.photos.di;

import dagger.Component;
import us.mikeandwan.photos.ui.categories.CategoryListActivity;
import us.mikeandwan.photos.ui.initialLoad.InitialLoadActivity;
import us.mikeandwan.photos.ui.login.LoginActivity;
import us.mikeandwan.photos.ui.loginCallback.LoginCallbackActivity;
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity;
import us.mikeandwan.photos.ui.photos.CommentDialogFragment;
import us.mikeandwan.photos.ui.photos.ExifDialogFragment;
import us.mikeandwan.photos.ui.photos.PhotoListActivity;
import us.mikeandwan.photos.ui.photos.RatingDialogFragment;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = { ActivityModule.class, AdapterModule.class })
public interface ActivityComponent {
    // activities
    void inject(CategoryListActivity activity);
    void inject(InitialLoadActivity activity);
    void inject(LoginActivity activity);
    void inject(LoginCallbackActivity activity);
    void inject(ModeSelectionActivity activity);
    void inject(PhotoListActivity activity);

    // fragments
    void inject(CommentDialogFragment fragment);
    void inject(ExifDialogFragment fragment);
    void inject(RatingDialogFragment fragment);
}
