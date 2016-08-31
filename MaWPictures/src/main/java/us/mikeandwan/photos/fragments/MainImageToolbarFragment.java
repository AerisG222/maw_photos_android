package us.mikeandwan.photos.fragments;

import android.widget.ImageButton;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import us.mikeandwan.photos.R;


@SuppressWarnings("ALL")
@EFragment(R.layout.fragment_main_image_toolbar)
public class MainImageToolbarFragment extends BasePhotoFragment {
    private boolean _isSlideshowPlaying;

    @ViewById(R.id.commentButton)
    protected ImageButton _commentButton;

    @ViewById(R.id.exifButton)
    protected ImageButton _exifButton;

    @ViewById(R.id.rotateLeftButton)
    protected ImageButton _rotateLeftButton;

    @ViewById(R.id.rotateRightButton)
    protected ImageButton _rotateRightButton;

    @ViewById(R.id.ratingButton)
    protected ImageButton _ratingButton;

    @ViewById(R.id.slideshowButton)
    protected ImageButton _slideshowButton;


    @Click(R.id.exifButton)
    void onExifButtonClick() {
        getPhotoActivity().showExif();
    }

    @Click(R.id.ratingButton)
    void onRatingButtonClick() {
        getPhotoActivity().showRating();
    }

    @Click(R.id.commentButton)
    void onCommentButtonClick() {
        getPhotoActivity().showComments();
    }

    @Click(R.id.rotateLeftButton)
    void onRotateLeftButtonClick() {
        getPhotoActivity().rotatePhoto(-1);
    }

    @Click(R.id.rotateRightButton)
    void onRotateRightButtonClick() {
        getPhotoActivity().rotatePhoto(1);
    }

    @Click(R.id.slideshowButton)
    void onSlideshowButtonClick() {
        toggleSlideshow();
        getPhotoActivity().toggleSlideshow();
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
    }
}
