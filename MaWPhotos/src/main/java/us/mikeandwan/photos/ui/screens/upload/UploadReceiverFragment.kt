package us.mikeandwan.photos.ui.screens.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import us.mikeandwan.photos.databinding.FragmentUploadReceiverBinding

@AndroidEntryPoint
class UploadReceiverFragment : Fragment() {
    companion object {
        fun newInstance() = UploadReceiverFragment()
    }

    private lateinit var binding: FragmentUploadReceiverBinding
    val viewModel by viewModels<UploadViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUploadReceiverBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        return binding.root
    }
}