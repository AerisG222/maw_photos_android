package us.mikeandwan.photos.fragments;

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
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.di.TaskComponent;
import us.mikeandwan.photos.models.Rating;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.tasks.GetRatingTask;
import us.mikeandwan.photos.tasks.SetRatingTask;


public class RatingDialogFragment extends BasePhotoDialogFragment {
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Unbinder _unbinder;

    @BindView(R.id.yourRatingBar) RatingBar _yourRatingBar;
    @BindView(R.id.averageRatingBar) RatingBar _averageRatingBar;

    @Inject GetRatingTask _getRatingTask;
    @Inject SetRatingTask _setRatingTask;
    @Inject AuthenticationExceptionHandler _authHandler;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rating, container, false);
        _unbinder = ButterKnife.bind(this, view);

        _yourRatingBar.setOnRatingBarChangeListener((_ratingBar, rating, fromUser) -> {
            if (fromUser) {
                disposables.add(Flowable.fromCallable(() -> _setRatingTask.call(getCurrentPhoto().getId(), Math.round(rating)))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                x -> displayRating(x),
                                ex -> _authHandler.handleException(ex)
                        )
                );

                updateProgress();
            }
        });

        getDialog().setTitle("Ratings");

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(TaskComponent.class).inject(this);
    }


    @Override
    public void onResume() {
        // http://stackoverflow.com/a/24213921
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        getRatings();

        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
    }


    private void getRatings() {
        _yourRatingBar.setRating(0);
        _averageRatingBar.setRating(0);

        disposables.add(Flowable.fromCallable(() -> _getRatingTask.call(getCurrentPhoto().getId()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> displayRating(x),
                        ex -> _authHandler.handleException(ex)
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
