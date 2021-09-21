package us.mikeandwan.photos.uinew.ui.random

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import us.mikeandwan.photos.R

class RandomFragment : Fragment() {

    companion object {
        fun newInstance() = RandomFragment()
    }

    private lateinit var viewModel: RandomViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_random, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(RandomViewModel::class.java)
        // TODO: Use the ViewModel
    }

}