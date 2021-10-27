package us.mikeandwan.photos.uiold.photos

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

// this was needed to properly scroll immediately on resume (like during an orientation change)
class ThumbnailLinearLayoutManager(
    context: Context?,
    orientation: Int,
    reverseLayout: Boolean,
    private val _startPosition: Int
) : LinearLayoutManager(context, orientation, reverseLayout) {
    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        super.onLayoutChildren(recycler, state)
        if (_startPosition > 0) {
            scrollToPosition(_startPosition)
        }
    }
}