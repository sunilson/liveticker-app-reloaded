package at.sunilson.liveticker.location

import android.location.Address
import android.location.Geocoder
import at.sunilson.liveticker.core.models.Coordinates
import at.sunilson.liveticker.core.models.Location
import com.github.kittinunf.result.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

interface AddressResolver {
    suspend fun resolveLocationForCoordinates(coordinates: Coordinates): Result<Location, Exception>
    suspend fun resolveLocationForAddress(address: String): Result<Location, Exception>
}

internal class AddressResolverImpl(private val geocoder: Geocoder) : AddressResolver {

    class NoResultFound : Exception()

    override suspend fun resolveLocationForAddress(address: String): Result<Location, Exception> =
        withContext(Dispatchers.Default) {
            val addresses = try {
                geocoder.getFromLocationName(address, 1)
            } catch (error: Exception) {
                return@withContext Result.error(error)
            }

            if (addresses.isNotEmpty()) {
                Result.success(
                    Location(
                        Coordinates(addresses[0].latitude, addresses[0].longitude),
                        addresses[0].getAddressLine(0)
                    )
                )
            } else Result.error(NoResultFound())
        }

    override suspend fun resolveLocationForCoordinates(coordinates: Coordinates): Result<Location, Exception> =
        withContext(Dispatchers.Default) {
            val addresses = try {
                geocoder.getFromLocation(coordinates.lat, coordinates.lng, 1)
            } catch (error: Exception) {
                return@withContext Result.error(error)
            }

            if (addresses.isNotEmpty()) {
                Result.success(
                    Location(
                        Coordinates(addresses[0].latitude, addresses[0].longitude),
                        addresses[0].getAddressLine(0)
                    )
                )
            } else Result.error(NoResultFound())
        }
}