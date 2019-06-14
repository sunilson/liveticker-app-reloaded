package at.sunilson.liveticker.location

import android.location.Address
import android.location.Geocoder
import at.sunilson.liveticker.core.models.Location
import com.github.kittinunf.result.Result
import com.google.android.gms.maps.model.LatLng

interface AddressResolver {
    operator fun invoke(latLng: LatLng): Result<Location, Exception>
    fun resolveAddressForCoordinates(latLng: LatLng): Result<Location, Exception>
}

internal class AddressResolverImpl(private val geocoder: Geocoder) : AddressResolver {
    override fun invoke(latLng: LatLng) = resolveAddressForCoordinates(latLng)
    override fun resolveAddressForCoordinates(latLng: LatLng): Result<Location, Exception> {
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        } catch (error: Exception) {
            return Result.error(error)
        }

        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            return Result.of(
                Location(
                    address.latitude,
                    address.longitude,
                    address.featureName
                )
            )
        }

        return Result.error(Exception())
    }
}