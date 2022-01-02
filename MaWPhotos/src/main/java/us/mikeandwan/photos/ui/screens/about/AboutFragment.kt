package us.mikeandwan.photos.ui.screens.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import us.mikeandwan.photos.R
import us.mikeandwan.photos.databinding.FragmentAboutBinding

@AndroidEntryPoint
class AboutFragment : Fragment() {
    companion object {
        fun newInstance() = AboutFragment()
    }

    private lateinit var binding: FragmentAboutBinding
    private val viewModel by viewModels<AboutViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        initStateObservers()

        return binding.root
    }

    private fun initStateObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                withContext(Dispatchers.IO) {
                    val history = resources
                        .openRawResource(R.raw.release_notes)
                        .bufferedReader()
                        .use { it.readText() }

                    viewModel.setHistory(history)
                }
            }
        }
    }
}