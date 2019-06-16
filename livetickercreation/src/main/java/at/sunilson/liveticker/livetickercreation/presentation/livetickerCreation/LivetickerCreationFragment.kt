package at.sunilson.liveticker.livetickercreation.presentation.livetickerCreation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.livetickercreation.R
import at.sunilson.liveticker.livetickercreation.databinding.FragmentLivetickerCreationBinding
import at.sunilson.liveticker.location.MapFragmentCreator
import at.sunilson.liveticker.location.MapOptions
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.enterChildViewsFromBottomDelayed
import kotlinx.android.synthetic.main.fragment_liveticker_creation.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LivetickerCreationFragment : BaseFragment() {

    private val viewModel: LivetickerCreationViewModel by sharedViewModel()
    private val mapFragmentCreator: MapFragmentCreator by inject()
    private var animated: Boolean = false

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

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer { findNavController().popBackStack() })

        viewModel.location.observe(viewLifecycleOwner, Observer {

        })

        fragment_liveticker_creation_map.setOnClickListener {
            //Open location picker. Uses livetickerCreationViewmodel to show results
            findNavController().navigate(R.id.action_livetickerCreationFragment_to_locationPickerDialogFragment)
        }

        if (!animated) {
            fragment_liveticker_creation_inputs.enterChildViewsFromBottomDelayed {
                animated = true
                //Prevent IllegalStateExceptions
                if (!isStateSaved && isAdded) {
                    //Add lite map fragment after intro animation to make sure transition is more smooth
                    childFragmentManager
                        .beginTransaction()
                        .replace(R.id.fragment_liveticker_creation_map, mapFragmentCreator(MapOptions(true)))
                        .commit()
                }
            }
        }

        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.reset()
    }
}