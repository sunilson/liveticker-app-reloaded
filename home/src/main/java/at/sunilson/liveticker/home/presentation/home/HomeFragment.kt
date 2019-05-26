package at.sunilson.liveticker.home.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.home.R
import at.sunilson.liveticker.home.databinding.FragmentHomeBinding
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentHomeBinding>(inflater, R.layout.fragment_home, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                HomeViewModel.AddLiveTicker -> findNavController().navigate(R.id.action_homeFragment_to_livetickerCreationFragment)
            }
        })
    }
}