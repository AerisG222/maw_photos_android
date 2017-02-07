package us.mikeandwan.photos.ui.photos;


import java.util.List;

import us.mikeandwan.photos.models.Photo;


public interface IPhotoActivity {
    void updateProgress();

    Photo getCurrentPhoto();

    List<Photo> getPhotoList();

    int getCurrentIndex();

    boolean hasNext();

    boolean hasPrevious();

    void gotoPhoto(int index);

    void showExif();

    void showComments();

    void showRating();

    void rotatePhoto(int direction);

    void toggleSlideshow();
}