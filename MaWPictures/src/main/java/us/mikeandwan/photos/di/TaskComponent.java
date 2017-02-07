package us.mikeandwan.photos.di;

import dagger.Component;
import us.mikeandwan.photos.ui.categories.CategoryListActivity;
import us.mikeandwan.photos.ui.login.LoginActivity;
import us.mikeandwan.photos.ui.mode.ModeSelectionActivity;
import us.mikeandwan.photos.ui.photos.PhotoListActivity;
import us.mikeandwan.photos.ui.photos.CommentDialogFragment;
import us.mikeandwan.photos.ui.photos.ExifDialogFragment;
import us.mikeandwan.photos.ui.photos.MainImageFragment;
import us.mikeandwan.photos.ui.photos.RatingDialogFragment;
import us.mikeandwan.photos.ui.photos.ThumbnailListFragment;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {AdapterModule.class, TaskModule.class} )
public interface TaskComponent {
    // https://github.com/google/dagger/issues/468
    // https://code.google.com/p/android/issues/detail?id=223549

    // Ooph!  I thought I was not following how to properly setup DI for dependant components, i.e.
    // having TaskComponent depend on ActivityComponent, but the errors I encountered were really a
    // bug in Jack / eclipse java compiler!  The 2 links above track the outstanding issues, and
    // once those are fixed, we should re-introduce the notion of dependent components based on the
    // activity component using the following guide:
    // http://fernandocejas.com/2015/04/11/tasting-dagger-2-on-android/

    // activities
    void inject(CategoryListActivity activity);
    void inject(LoginActivity activity);
    void inject(ModeSelectionActivity activity);
    void inject(PhotoListActivity activity);

    // fragments
    void inject(CommentDialogFragment fragment);
    void inject(ExifDialogFragment fragment);
    void inject(MainImageFragment fragment);
    void inject(RatingDialogFragment fragment);
    void inject(ThumbnailListFragment fragment);
}
