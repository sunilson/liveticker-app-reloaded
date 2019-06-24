package at.sunilson.liveticker.location

import android.annotation.SuppressLint
import android.location.Location
import com.github.kittinunf.result.Result
import com.google.android.gms.location.FusedLocationProviderClient

interface LocationFinder {
    operator fun invoke(cb: (Result<Location, Exception>) -> Unit)
    fun findUserLocation(cb: (Result<Location, Exception>) -> Unit)
}

internal class LocationFinderImpl(private val client: FusedLocationProviderClient) : LocationFinder {
    override fun invoke(cb: (Result<Location, Exception>) -> Unit) = findUserLocation(cb)

    @SuppressLint("MissingPermission")
    override fun findUserLocation(cb: (Result<Location, Exception>) -> Unit) {
        client.lastLocation.addOnCompleteListener {
            when {
                it.isSuccessful -> cb(Result.success(it.result ?: return@addOnCompleteListener))
                it.exception != null -> cb(Result.error(it.exception ?: return@addOnCompleteListener))
                else -> cb(Result.error(Exception()))
            }
        }
    }
}