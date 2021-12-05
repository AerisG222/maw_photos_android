package us.mikeandwan.photos.ui.controls.toolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
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

        return binding.root
    }
}