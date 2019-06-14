package at.sunilson.liveticker.location

import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.*

val locationModule = module {
    single(createdAtStart = true) {
        Places.initialize(androidApplication(), androidApplication().getString(R.string.places))
        Places.createClient(androidApplication())
    }

    single<MapFragmentCreator> { MapFragmentCreatorImpl() }
    single<LocationFinder> { LocationFinderImpl(get()) }
    single<AddressResolver> { AddressResolverImpl(get()) }

    single { LocationServices.getFusedLocationProviderClient(androidApplication()) }
    single { Geocoder(androidApplication(), Locale.getDefault()) }
}