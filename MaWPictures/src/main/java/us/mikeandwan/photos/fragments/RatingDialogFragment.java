package us.mikeandwan.photos.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.activities.LoginActivity;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Rating;
import us.mikeandwan.photos.services.MawAuthenticationException;
import us.mikeandwan.photos.tasks.GetRatingTask;
import us.mikeandwan.photos.tasks.SetRatingTask;


public class RatingDialogFragment extends BasePhotoDialogFragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Unbinder _unbinder;

    @BindView(R.id.yourRatingBar) RatingBar _yourRatingBar;
    @BindView(R.id.averageRatingBar) RatingBar _averageRatingBar;

    @Inject GetRatingTask _getRatingTask;
    @Inject SetRatingTask _setRatingTask;


    protected void afterBind() {
        _yourRatingBar.setOnRatingBarChangeListener((_ratingBar, rating, fromUser) -> {
                if (fromUser) {
                    disposables.add(Flowable.fromCallable(() -> _setRatingTask.call(getCurrentPhoto().getId(), Math.round(rating)))
                            .subscribeOn(Schedulers.io())
                            .observeOn(Schedulers.single())
                            .subscribe(
                                    x -> displayRating(x),
                                    ex -> handleException(ex)
                            )
                    );

                    updateProgress();
                }
        });

        getDialog().setTitle("Ratings");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rating, container, false);
        _unbinder = ButterKnife.bind(this, view);

        afterBind();

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(TaskComponent.class).inject(this);
    }


    @Override
    public void onResume() {
        getRatings();

        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
    }


    private void handleException(Throwable ex) {
        if (ex.getCause() instanceof MawAuthenticationException) {
            startActivity(new Intent(getContext(), LoginActivity.class));
        }
    }


    private void getRatings() {
        _yourRatingBar.setRating(0);
        _averageRatingBar.setRating(0);

        disposables.add(Flowable.fromCallable(() -> _getRatingTask.call(getCurrentPhoto().getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.single())
                .subscribe(
                        x -> displayRating(x),
                        ex -> handleException(ex)
                )
        );

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
