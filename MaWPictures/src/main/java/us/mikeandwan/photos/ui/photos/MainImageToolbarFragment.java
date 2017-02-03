package us.mikeandwan.photos.ui.photos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import us.mikeandwan.photos.R;


public class MainImageToolbarFragment extends BasePhotoFragment {
    private boolean _isSlideshowPlaying;
    private Unbinder _unbinder;

    @BindView(R.id.commentButton) ImageButton _commentButton;
    @BindView(R.id.exifButton) ImageButton _exifButton;
    @BindView(R.id.rotateLeftButton) ImageButton _rotateLeftButton;
    @BindView(R.id.rotateRightButton) ImageButton _rotateRightButton;
    @BindView(R.id.ratingButton) ImageButton _ratingButton;
    @BindView(R.id.slideshowButton)  ImageButton _slideshowButton;

    @OnClick(R.id.exifButton) void onExifButtonClick() {
        getPhotoActivity().showExif();
    }

    @OnClick(R.id.ratingButton) void onRatingButtonClick() {
        getPhotoActivity().showRating();
    }

    @OnClick(R.id.commentButton) void onCommentButtonClick() {
        getPhotoActivity().showComments();
    }

    @OnClick(R.id.rotateLeftButton) void onRotateLeftButtonClick() {
        getPhotoActivity().rotatePhoto(-1);
    }

    @OnClick(R.id.rotateRightButton) void onRotateRightButtonClick() {
        getPhotoActivity().rotatePhoto(1);
    }

    @OnClick(R.id.slideshowButton) void onSlideshowButtonClick() {
        toggleSlideshow();
        getPhotoActivity().toggleSlideshow();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_image_toolbar, container, false);
        _unbinder = ButterKnife.bind(this, view);

        return view;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
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
    }
}
