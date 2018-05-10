package us.mikeandwan.photos.ui.photos;


import java.util.List;

import us.mikeandwan.photos.models.Photo;


public interface IPhotoActivity {
    void addWork();

    void removeWork();

    void onApiException(Throwable throwable);

    Photo getCurrentPhoto();

    List<Photo> getPhotoList();
}
