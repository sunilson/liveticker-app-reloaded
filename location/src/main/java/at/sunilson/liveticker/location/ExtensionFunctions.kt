package at.sunilson.liveticker.location

import at.sunilson.liveticker.core.models.Coordinates
import com.google.android.gms.maps.model.LatLng

fun Coordinates.toLatLng() = LatLng(lat, lng)
fun LatLng.toCoordinates() = Coordinates(latitude, longitude)