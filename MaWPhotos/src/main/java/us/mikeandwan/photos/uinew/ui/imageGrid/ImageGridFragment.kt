package us.mikeandwan.photos.uinew.ui.imageGrid

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.flexbox.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentImageGridBinding

@AndroidEntryPoint
class ImageGridFragment : Fragment() {
    companion object {
        fun newInstance() = ImageGridFragment()
    }

    private val _clickHandlerForwarder = ImageGridRecyclerAdapter.ClickListener { _clickHandler?.onClick(it) }
    private var _clickHandler: ImageGridRecyclerAdapter.ClickListener? = null
    private var _thumbSize = 0
    private val _width = MutableStateFlow(0)
    private var _listener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private lateinit var binding: FragmentImageGridBinding
    val viewModel by viewModels<ImageGridViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentImageGridBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.imageGridRecyclerView.layoutManager = FlexboxLayoutManager(activity).apply {
            flexWrap = FlexWrap.WRAP
            flexDirection = FlexDirection.ROW
            alignItems = AlignItems.STRETCH
            justifyContent = JustifyContent.SPACE_BETWEEN
        }

        _thumbSize = resources.getDimension(R.dimen.category_grid_thumbnail_size).toInt()

        initStateObservers()
        listenForWidth()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

       viewModel.setDoInvalidate(true)
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                _width
                    .onEach {
                        updateAdapter()
                    }
                    .launchIn(this)

                viewModel.doInvalidate
                    .onEach {
                        delay(1)
                        if(it) {
                            viewModel.setDoInvalidate(false)

                            val adapter = binding.imageGridRecyclerView.adapter as ImageGridRecyclerAdapter
                            adapter.submitList(viewModel.gridItems.value)
                        }
                    }
                    .launchIn(this)
            }
        }
    }

    private fun listenForWidth() {
        _listener = ViewTreeObserver.OnGlobalLayoutListener {
            binding.container.viewTreeObserver.removeOnGlobalLayoutListener(_listener)
            _width.value = binding.container.width
        }

        binding.container.viewTreeObserver.addOnGlobalLayoutListener(_listener)
    }

    private fun updateAdapter() {
        binding.imageGridRecyclerView.adapter = ImageGridRecyclerAdapter(getThumbnailSize(), _clickHandlerForwarder)
    }

    private fun getThumbnailSize(): Int {
        val width = _width.value
        val cols = maxOf(1, width / _thumbSize)

        val totalInteriorMargins = cols * resources.getDimension(R.dimen._2dp)
        val remainingSpaceForImages = width - totalInteriorMargins.toInt()

        return remainingSpaceForImages / cols
    }

    fun setClickHandler(handler: ImageGridRecyclerAdapter.ClickListener) {
        _clickHandler = handler
    }

    fun setData(data: List<ImageGridItem>) {
        viewModel.setData(data)
    }
}