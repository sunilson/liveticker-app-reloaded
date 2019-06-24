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
import at.sunilson.liveticker.location.toLatLng
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import at.sunilson.liveticker.presentation.enterChildViewsFromBottomDelayed
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_liveticker_creation.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class LivetickerCreationFragment : BaseFragment<LivetickerCreationViewModel>() {

    override val viewModel: LivetickerCreationViewModel by sharedViewModel()
    private val mapFragmentCreator: MapFragmentCreator by inject()
    private var animated: Boolean = false
    private var map: GoogleMap? = null
    private var marker: Marker? = null

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

        setStatusBarColor(R.color.statusBarColor)

        val mapFragment = mapFragmentCreator(MapOptions(true))
        mapFragment.getMapAsync { this.map = it }

        viewModel.location.observe(viewLifecycleOwner, Observer {
            marker?.remove()
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(it.coordinates.toLatLng(), 10f))
            marker = map?.addMarker(MarkerOptions().position(it.coordinates.toLatLng()))
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
                        .replace(R.id.fragment_liveticker_creation_map, mapFragment)
                        .commit()
                }
            }
        }

        toolbar.setNavigationOnClickListener { findNavController().popBackStack() }
    }

    override fun onNavigationEvent(event: NavigationEvent) {
        findNavController().popBackStack()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.reset()
    }
}