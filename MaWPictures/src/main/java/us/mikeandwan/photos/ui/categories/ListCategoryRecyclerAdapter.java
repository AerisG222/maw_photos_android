package us.mikeandwan.photos.ui.categories;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import us.mikeandwan.photos.R;
import us.mikeandwan.photos.models.Category;
import us.mikeandwan.photos.services.AuthenticationExceptionHandler;
import us.mikeandwan.photos.services.DataServices;
import us.mikeandwan.photos.services.PhotoStorage;


public class ListCategoryRecyclerAdapter extends CategoryRecyclerAdapter<ListCategoryRecyclerAdapter.ViewHolder> {
    public ListCategoryRecyclerAdapter(Context context,
                                       PhotoStorage photoStorage,
                                       DataServices dataServices,
                                       AuthenticationExceptionHandler authHandler) {
        super(context, photoStorage, dataServices, authHandler);
    }


    @Override
    public ListCategoryRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View categoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false);

        return new ViewHolder(categoryView);
    }


    protected void displayCategory(Category category, ListCategoryRecyclerAdapter.ViewHolder viewHolder) {
        String file = "file://" + _photoStorage.getCachePath(category.getTeaserPhotoInfo().getPath());

        viewHolder._nameTextView.setText(category.getName());

        Picasso
                .with(_context)
                .load(file)
                .resizeDimen(R.dimen.category_list_thumbnail_size, R.dimen.category_list_thumbnail_size)
                .centerCrop()
                .into(viewHolder._thumbnailImageView);
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.thumbnailImageView) ImageView _thumbnailImageView;
        @BindView(R.id.categoryNameTextView) TextView _nameTextView;


        ViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }
}
