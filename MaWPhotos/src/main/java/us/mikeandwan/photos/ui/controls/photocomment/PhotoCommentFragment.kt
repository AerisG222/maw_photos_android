package us.mikeandwan.photos.ui.controls.photocomment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TableRow
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentPhotoCommentBinding
import us.mikeandwan.photos.domain.models.PhotoComment
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class PhotoCommentFragment : Fragment() {
    companion object {
        fun newInstance() = PhotoCommentFragment()
    }

    private lateinit var binding: FragmentPhotoCommentBinding
    val viewModel by viewModels<PhotoCommentViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPhotoCommentBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        initStateObservers()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.comments
                    .onEach { displayComments(it) }
                    .launchIn(this)
            }
        }
    }

    private fun displayComments(comments: List<PhotoComment>) {
        binding.commentTableLayout.removeAllViews()
        binding.commentScrollView.scrollTo(0, 0)

        val df: DateFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)

        comments.forEachIndexed { idx, comment ->
            val ctx: Context = requireActivity()
            val titleRow = TableRow(ctx)
            val commentRow = TableRow(ctx)

            if (idx % 2 == 1) {
                titleRow.setBackgroundColor(-0xddddde)
                commentRow.setBackgroundColor(-0xddddde)
            }

            binding.commentTableLayout.addView(titleRow)
            binding.commentTableLayout.addView(commentRow)

            val usernameView = TextView(ctx)
            usernameView.setPadding(4, 4, 4, 4)
            usernameView.typeface = Typeface.DEFAULT_BOLD
            usernameView.text = comment.username

            val dateView = TextView(ctx)
            dateView.setPadding(4, 4, 4, 4)
            dateView.typeface = Typeface.DEFAULT_BOLD
            dateView.text = df.format(comment.entryDate)
            dateView.gravity = Gravity.END
            titleRow.addView(usernameView)
            titleRow.addView(dateView)

            val commentView = TextView(ctx)
            commentView.setPadding(4, 4, 4, 4)
            commentView.text = comment.commentText
            commentRow.addView(commentView)
            (commentView.layoutParams as TableRow.LayoutParams).span = 2
        }
    }
}