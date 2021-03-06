package us.mikeandwan.photos.ui.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import us.mikeandwan.photos.di.ActivityComponent;
import us.mikeandwan.photos.models.Rating;
import us.mikeandwan.photos.services.DataServices;


public class RatingDialogFragment extends BasePhotoDialogFragment {
    private final CompositeDisposable _disposables = new CompositeDisposable();
    private Unbinder _unbinder;

    @BindView(R.id.yourRatingBar) RatingBar _yourRatingBar;
    @BindView(R.id.averageRatingBar) RatingBar _averageRatingBar;

    @Inject DataServices _dataServices;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_rating, container, false);
        _unbinder = ButterKnife.bind(this, view);

        _yourRatingBar.setOnRatingBarChangeListener((_ratingBar, rating, fromUser) -> {
            if (fromUser) {
                _disposables.add(Flowable.fromCallable(() -> {
                            addWork();
                            return _dataServices.setRating(getCurrentPhoto().getId(), Math.round(rating));
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                x -> {
                                    removeWork();
                                    displayRating(x);
                                },
                                ex -> {
                                    removeWork();
                                    getPhotoActivity().onApiException(ex);
                                }
                        )
                );
            }
        });

        getDialog().setTitle("Ratings");

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getComponent(ActivityComponent.class).inject(this);
    }


    @Override
    public void onResume() {
        // http://stackoverflow.com/a/24213921
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes(params);

        getRatings();

        super.onResume();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        _disposables.clear(); // do not send event after activity has been destroyed
        _unbinder.unbind();
    }


    private void getRatings() {
        _yourRatingBar.setRating(0);
        _averageRatingBar.setRating(0);

        _disposables.add(Flowable.fromCallable(() -> {
                    addWork();
                    return _dataServices.getRating(getCurrentPhoto().getId());
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        x -> {
                            removeWork();
                            displayRating(x);
                        },
                        ex -> {
                            removeWork();
                            getPhotoActivity().onApiException(ex);
                        }
                )
        );
    }


    private void displayRating(Rating rating) {
        if (rating == null) {
            _yourRatingBar.setRating(0);
            _averageRatingBar.setRating(0);
        } else {
            _yourRatingBar.setRating(rating.getUserRating());
            _averageRatingBar.setRating(rating.getAverageRating());
        }
    }
}
