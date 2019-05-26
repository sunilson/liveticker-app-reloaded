package at.sunilson.liveticker.home.presentation.livetickerCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.home.R
import at.sunilson.liveticker.home.databinding.FragmentLivetickerCreationBinding
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LivetickerCreationFragment : BaseFragment() {

    private val viewModel: LivetickerCreationViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentLivetickerCreationBinding>(
            inflater,
            R.layout.fragment_liveticker_creation,
            container
        )
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            findNavController().popBackStack()
        })
    }
}