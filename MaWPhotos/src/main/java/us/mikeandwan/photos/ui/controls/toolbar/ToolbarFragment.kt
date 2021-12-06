package us.mikeandwan.photos.ui.controls.toolbar

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import us.mikeandwan.photos.databinding.FragmentToolbarBinding

@AndroidEntryPoint
class ToolbarFragment : Fragment() {
    companion object {
        fun newInstance() = ToolbarFragment()
    }

    private lateinit var binding: FragmentToolbarBinding
    private val viewModel by viewModels<ToolbarViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentToolbarBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initStateObservers()

        return binding.root
    }

    fun initStateObservers() {
        binding.searchEditText.setOnEditorActionListener { v, actionId, evt ->
            val query = v.text.toString()

            if (query.isBlank()) {
                // prevent soft keyboard from being dismissed
                true
            } else {
                viewModel.search(query)

                // dismiss soft keyboard
                false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.closeKeyboardSignal
                    .filter { it }
                    .onEach {
                        closeKeyboard()
                        viewModel.closeKeyboardSignalHandled()
                    }
                    .launchIn(this)
            }
        }
    }

    private fun closeKeyboard() {
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.hideSoftInputFromWindow(binding.root.windowToken, 0)
    }
}