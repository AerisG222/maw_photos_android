package us.mikeandwan.photos.fragments;

import android.content.Intent;
import android.util.Log;
import android.widget.RatingBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.concurrent.ExecutionException;

import us.mikeandwan.photos.MawApplication;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity_;
import us.mikeandwan.photos.data.Rating;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.tasks.BackgroundTaskExecutor;
import us.mikeandwan.photos.tasks.GetRatingBackgroundTask;
import us.mikeandwan.photos.tasks.SetRatingBackgroundTask;


@EFragment(R.layout.dialog_rating)
public class RatingDialogFragment extends BasePhotoDialogFragment {
    @ViewById(R.id.yourRatingBar)
    protected RatingBar _yourRatingBar;

    @ViewById(R.id.averageRatingBar)
    protected RatingBar _averageRatingBar;


    @AfterViews
    protected void afterViews() {
        _yourRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser) {
                    int photoId = getCurrentPhoto().getId();

                    SetRatingBackgroundTask task = new SetRatingBackgroundTask(getContext(), photoId, Math.round(rating)) {
                        @Override
                        protected void postExecuteTask(Rating rating) {
                            displayRating(rating);
                        }

                        @Override
                        protected void handleException(ExecutionException ex) {
                            Log.e(MawApplication.LOG_TAG, "exception setting the rating: " + ex.getMessage());

                            if (ex.getCause() instanceof MawAuthenticationException) {
                                startActivity(new Intent(getContext(), LoginActivity_.class));
                            }
                        }
                    };

                    BackgroundTaskExecutor.getInstance().enqueueTask(task);
                    updateProgress();
                }
            }
        });

        getDialog().setTitle("Ratings");
    }


    @Override
    public void onResume() {
        getRatings();

        super.onResume();
    }


    private void getRatings() {
        _yourRatingBar.setRating(0);
        _averageRatingBar.setRating(0);

        GetRatingBackgroundTask task = new GetRatingBackgroundTask(getContext(), getCurrentPhoto().getId()) {
            @Override
            protected void postExecuteTask(Rating rating) {
                displayRating(rating);
            }

            @Override
            protected void handleException(ExecutionException ex) {
                Log.e(MawApplication.LOG_TAG, "exception getting the rating: " + ex.getMessage());

                if (ex.getCause() instanceof MawAuthenticationException) {
                    startActivity(new Intent(getContext(), LoginActivity_.class));
                }
            }
        };

        BackgroundTaskExecutor.getInstance().enqueueTask(task);
        updateProgress();
    }


    private void displayRating(Rating rating) {
        if (rating == null) {
            _yourRatingBar.setRating(0);
            _averageRatingBar.setRating(0);
        } else {
            _yourRatingBar.setRating(rating.getUserRating());
            _averageRatingBar.setRating(rating.getAverageRating());
        }

        updateProgress();
    }
}
