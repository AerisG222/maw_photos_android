package us.mikeandwan.photos.di;

import dagger.Component;
import us.mikeandwan.photos.activities.CategoryListActivity;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.activities.ModeSelectionActivity;
import us.mikeandwan.photos.activities.PhotoListActivity;
import us.mikeandwan.photos.fragments.CategoryListFragment;
import us.mikeandwan.photos.fragments.CategoryThumbnailsFragment;
import us.mikeandwan.photos.fragments.CommentDialogFragment;
import us.mikeandwan.photos.fragments.ExifDialogFragment;
import us.mikeandwan.photos.fragments.RatingDialogFragment;
import us.mikeandwan.photos.fragments.ThumbnailListFragment;


@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = TaskModule.class )
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
    void inject(CategoryListFragment fragment);
    void inject(CategoryThumbnailsFragment fragment);
    void inject(CommentDialogFragment fragment);
    void inject(ExifDialogFragment fragment);
    void inject(RatingDialogFragment fragment);
    void inject(ThumbnailListFragment fragment);
}
