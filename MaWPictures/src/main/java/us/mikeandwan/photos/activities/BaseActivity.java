package us.mikeandwan.photos.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

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
}
