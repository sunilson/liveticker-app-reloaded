package at.sunilson.liveticker.livetickercreation.presentation.locationPickerDialog

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.core.REQUEST_PERMISSIONS
import at.sunilson.liveticker.livetickercreation.R
import at.sunilson.liveticker.livetickercreation.databinding.DialogFragmentLocationPickerBinding
import at.sunilson.liveticker.livetickercreation.presentation.livetickerCreation.LivetickerCreationViewModel
import at.sunilson.liveticker.location.LocationFinder
import at.sunilson.liveticker.location.MapFragmentCreator
import at.sunilson.liveticker.location.MapOptions
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.hasPermission
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * Displays a map and a search bar to the user in which a location can be selected
 */
class LocationPickerFragment : BaseFragment() {

    private val livetickerCreationViewModel: LivetickerCreationViewModel by sharedViewModel()
    private val viewModel: LocationPickerDialogViewModel by viewModel()
    private val mapCreator: MapFragmentCreator by inject()
    private val locationFinder: LocationFinder by inject()

    private var map: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<DialogFragmentLocationPickerBinding>(
            inflater,
            R.layout.dialog_fragment_location_picker,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Show above previous fragment for animation purposes
        ViewCompat.setTranslationZ(view, 200f)

        val mapFragment = mapCreator(MapOptions(false))

        childFragmentManager
            .beginTransaction()
            .replace(R.id.dialog_fragment_location_picker_map, mapFragment)
            .commit()

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                is LocationPickerDialogViewModel.SearchClicked -> {
                    //Show google autocomplete to user, where a location can be selected
                    startActivityForResult(
                        Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.OVERLAY,
                            listOf(Place.Field.ID, Place.Field.NAME)
                        ).build(context ?: return@Observer),
                        AUTOCOMPLETE_REQUEST_CODE
                    )
                }
                is LocationPickerDialogViewModel.LocationFound -> {
                    //Return result and close
                    livetickerCreationViewModel.location.postValue(it.location)
                    findNavController().popBackStack()
                }
            }
        })

        viewModel.selectedLocation.observe(viewLifecycleOwner, Observer {
            marker?.remove()
            marker = map?.addMarker(MarkerOptions().position(it))
        })

        mapFragment.getMapAsync {
            map = it
            findUser()
            it.setOnMapClickListener { viewModel.selectedLocation.postValue(it) }
        }
    }

    @SuppressLint("MissingPermission")
    private fun findUser() {

        if (context?.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) != true) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_PERMISSIONS)
            return
        }

        locationFinder {
            it.fold({ result ->
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result.latitude, result.longitude), 10f))
            }, {
                //TODO Show error message
            })
        }
    }

    //Handle autocomplete result
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                //Move to selected place
                val place = Autocomplete.getPlaceFromIntent(data ?: return)
                map?.moveCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 5f))
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                //TODO Show error message
            }
        }
    }

    //Handle location permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSIONS) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                findUser()
            } else {
                //TODO Show error message
            }
        }
    }

    companion object {
        const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}