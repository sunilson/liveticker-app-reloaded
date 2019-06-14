package at.sunilson.liveticker.location

import com.google.android.gms.maps.GoogleMapOptions
import com.google.android.gms.maps.SupportMapFragment

data class MapOptions(val lite: Boolean)

interface MapFragmentCreator {
    operator fun invoke(mapOptions: MapOptions): SupportMapFragment
    fun createMapFragment(mapOptions: MapOptions): SupportMapFragment
}

internal class MapFragmentCreatorImpl : MapFragmentCreator {
    override fun invoke(mapOptions: MapOptions) = createMapFragment(mapOptions)
    override fun createMapFragment(mapOptions: MapOptions): SupportMapFragment {
        return if (mapOptions.lite) CustomSupportMapFragment.newInstance(GoogleMapOptions().liteMode(mapOptions.lite))
        else SupportMapFragment.newInstance()
    }
}