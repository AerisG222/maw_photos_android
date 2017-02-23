package us.mikeandwan.photos.ui;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.di.ActivityModule;
import us.mikeandwan.photos.di.ApplicationComponent;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getApplicationComponent().inject(this);
    }


    protected ApplicationComponent getApplicationComponent() {
        return ((MawApplication)getApplication()).getApplicationComponent();
    }


    protected ActivityModule getActivityModule() {
        return new ActivityModule(this);
    }


    protected void updateToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            ViewCompat.setElevation(toolbar, 4);

            if(title != null) {
                toolbar.setTitle(title);
            }
        }
    }
}
