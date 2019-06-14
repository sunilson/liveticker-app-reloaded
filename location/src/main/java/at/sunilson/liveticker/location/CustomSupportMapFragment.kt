package at.sunilson.liveticker.location

import android.os.Bundle
import android.view.View
import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment

class CustomSupportMapFragment : SupportMapFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.isClickable = false
        getMapAsync { it.uiSettings.isMapToolbarEnabled = false }
    }

    companion object {
        fun newInstance(options: GoogleMapOptions): CustomSupportMapFragment {
            return CustomSupportMapFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("MapOptions", options)
                }
            }
        }
    }
}