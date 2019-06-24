package at.sunilson.liveticker.location

import android.location.Address
import android.location.Geocoder
import at.sunilson.liveticker.core.models.Coordinates
import at.sunilson.liveticker.core.models.Location
import com.github.kittinunf.result.Result
import com.google.android.gms.maps.model.LatLng

interface AddressResolver {
    operator fun invoke(coordinates: Coordinates): Result<Location, Exception>
    fun resolveAddressForCoordinates(coordinates: Coordinates): Result<Location, Exception>
}

internal class AddressResolverImpl(private val geocoder: Geocoder) : AddressResolver {
    override fun invoke(coordinates: Coordinates) = resolveAddressForCoordinates(coordinates)
    override fun resolveAddressForCoordinates(coordinates: Coordinates): Result<Location, Exception> {
        val addresses: List<Address>
        try {
            addresses = geocoder.getFromLocation(coordinates.lat, coordinates.lng, 1)
        } catch (error: Exception) {
            return Result.error(error)
        }

        if (addresses.isNotEmpty()) {
            val address = addresses[0]
            return Result.success(
                Location(
                    Coordinates(address.latitude, address.longitude),
                    address.getAddressLine(0)
                )
            )
        }

        return Result.error(Exception())
    }
}