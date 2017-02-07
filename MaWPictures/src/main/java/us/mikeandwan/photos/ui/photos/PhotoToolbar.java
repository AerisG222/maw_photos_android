package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import us.mikeandwan.photos.R;


public class PhotoToolbar extends ConstraintLayout {
    private boolean _isSlideshowPlaying;
    private Unbinder _unbinder;
    private static final Object obj = new Object();

    private PublishSubject<Object> _exifSubject = PublishSubject.create();
    private PublishSubject<Object> _ratingSubject = PublishSubject.create();
    private PublishSubject<Object> _commentSubject = PublishSubject.create();
    private PublishSubject<Integer> _rotateSubject = PublishSubject.create();
    private PublishSubject<Boolean> _slideshowSubject = PublishSubject.create();

    @BindView(R.id.commentButton) ImageButton _commentButton;
    @BindView(R.id.exifButton) ImageButton _exifButton;
    @BindView(R.id.rotateLeftButton) ImageButton _rotateLeftButton;
    @BindView(R.id.rotateRightButton) ImageButton _rotateRightButton;
    @BindView(R.id.ratingButton) ImageButton _ratingButton;
    @BindView(R.id.slideshowButton)  ImageButton _slideshowButton;

    @OnClick(R.id.exifButton) void onExifButtonClick() {
        _exifSubject.onNext(obj);
    }
    @OnClick(R.id.ratingButton) void onRatingButtonClick() {
        _ratingSubject.onNext(obj);
    }
    @OnClick(R.id.commentButton) void onCommentButtonClick() { _commentSubject.onNext(obj); }
    @OnClick(R.id.rotateLeftButton) void onRotateLeftButtonClick() { _rotateSubject.onNext(-1); }
    @OnClick(R.id.rotateRightButton) void onRotateRightButtonClick() { _rotateSubject.onNext(1); }
    @OnClick(R.id.slideshowButton) void onSlideshowButtonClick() { toggleSlideshow(); }


    public PhotoToolbar(Context context, AttributeSet attribs) {
        super(context, attribs);

        LayoutInflater  mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mInflater.inflate(R.layout.view_photo_toolbar, this, true);

        _unbinder = ButterKnife.bind(this);
    }


    public Observable<Object> onExifClicked() { return _exifSubject.hide(); }
    public Observable<Object> onRatingClicked() { return _ratingSubject.hide(); }
    public Observable<Object> onCommentClicked() { return _commentSubject.hide(); }
    public Observable<Integer> onRotateClicked() { return _rotateSubject.hide(); }
    public Observable<Boolean> onToggleSlideshow() { return _slideshowSubject.hide(); }


    public void dispose() {
        _unbinder.unbind();
    }


    public void setSlideshowPlaying(boolean isPlaying) {
        _isSlideshowPlaying = !isPlaying;  // the internal value as the opposite
        toggleSlideshow();  // now toggle the opposite to correct internal state
    }


    private void toggleSlideshow() {
        _isSlideshowPlaying = !_isSlideshowPlaying;

        if (_isSlideshowPlaying) {
            _slideshowButton.setImageResource(R.drawable.ic_stop_white_24dp);
        } else {
            _slideshowButton.setImageResource(R.drawable.ic_play_arrow_white_24dp);
        }

        _slideshowSubject.onNext(_isSlideshowPlaying);
    }
}
