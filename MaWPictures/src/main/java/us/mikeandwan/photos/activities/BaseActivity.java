package us.mikeandwan.photos.activities;

import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.di.ApplicationComponent;
import us.mikeandwan.photos.di.TaskModule;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.getApplicationComponent().inject(this);
    }


    protected ApplicationComponent getApplicationComponent() {
        return ((MawApplication)getApplication()).getApplicationComponent();
    }


    protected TaskModule getTaskModule() {
        return new TaskModule(this);
    }


    protected void updateToolbar(Toolbar toolbar, String title) {
        if (toolbar != null) {
            setSupportActionBar(toolbar);

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(title);

            ViewCompat.setElevation(toolbar, 8);
        }
    }
}
