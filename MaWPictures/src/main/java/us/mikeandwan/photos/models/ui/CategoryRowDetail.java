package us.mikeandwan.photos.models.ui;

import android.widget.ImageView;

import us.mikeandwan.photos.models.Category;


public class CategoryRowDetail {
    private final ImageView _imageView;
    private final Category _category;


    public CategoryRowDetail(ImageView imageView, Category category) {
        _imageView = imageView;
        _category = category;
    }


    public ImageView getImageView() {
        return _imageView;
    }


    public Category getCategory() {
        return _category;
    }
}
