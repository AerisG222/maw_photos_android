package us.mikeandwan.photos.ui.photos;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


// this was needed to properly scroll immediately on resume (like during an orientation change)
public class ThumbnailLinearLayoutManager extends LinearLayoutManager {
    private int _startPosition;


    public ThumbnailLinearLayoutManager(Context context, int orientation, boolean reverseLayout, int startPosition) {
        super(context, orientation, reverseLayout);

        _startPosition = startPosition;
    }


    @Override
    public void onLayoutChildren(final RecyclerView.Recycler recycler, final RecyclerView.State state) {
        super.onLayoutChildren(recycler, state);

        if(_startPosition > 0) {
            scrollToPosition(_startPosition);
        }
    }
}
