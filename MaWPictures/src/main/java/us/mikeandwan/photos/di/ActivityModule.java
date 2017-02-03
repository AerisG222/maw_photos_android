package us.mikeandwan.photos.di;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;


@Module
class ActivityModule {
    private final Activity _activity;


    public ActivityModule(Activity activity) {
        this._activity = activity;
    }


    @Provides
    @PerActivity
    Activity provideActivity() {
        return this._activity;
    }
}
