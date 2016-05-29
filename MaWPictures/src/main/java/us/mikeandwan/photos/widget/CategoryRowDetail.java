package us.mikeandwan.photos.widget;


import android.widget.ImageView;
import android.widget.TextView;

import us.mikeandwan.photos.data.Category;

public class CategoryRowDetail {
    private ImageView _imageView;
    private TextView _textView;
    private Category _category;


    public CategoryRowDetail(ImageView imageView, TextView textView, Category category) {
        _imageView = imageView;
        _textView = textView;
        _category = category;
    }


    public ImageView getImageView() {
        return _imageView;
    }


    public TextView getTextView() {
        return _textView;
    }


    public Category getCategory() {
        return _category;
    }
}
