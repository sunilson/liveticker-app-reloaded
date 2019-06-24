package at.sunilson.liveticker.livetickercreation.presentation.locationPickerDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.models.Coordinates
import at.sunilson.liveticker.core.models.Location
import at.sunilson.liveticker.location.AddressResolver
import at.sunilson.liveticker.location.LocationFinder
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import com.github.kittinunf.result.failure
import com.github.kittinunf.result.success
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class LocationPickerDialogViewModel : BaseViewModel() {
    abstract val selectedLocation: MutableLiveData<Coordinates>

    abstract fun searchClicked(view: Any? = null)
    abstract fun tryFinish(view: Any? = null)
    abstract fun searchUser()

    object SearchClicked : NavigationEvent
    data class LocationFound(val location: Location) : NavigationEvent
    data class UserFound(val coordinates: Coordinates) : NavigationEvent
}

class LocationPickerDialogViewModelImpl(
    private val addressResolver: AddressResolver,
    private val locationFinder: LocationFinder
) :
    LocationPickerDialogViewModel() {

    override val selectedLocation: MutableLiveData<Coordinates> = MutableLiveData()

    override fun searchUser() {
        locationFinder {
            it.fold({ result ->
                navigationEvents.postValue(UserFound(Coordinates(result.latitude, result.longitude)))
            }, {
                //TODO Show error message
            })
        }
    }

    override fun searchClicked(view: Any?) {
        navigationEvents.postValue(SearchClicked)
    }

    override fun tryFinish(view: Any?) {
        loading.postValue(true)

        viewModelScope.launch {
            val selectedLocation = selectedLocation.value ?: return@launch
            val location = withContext(Dispatchers.Default) { addressResolver(selectedLocation) }

            location.fold(
                { navigationEvents.postValue(LocationFound(it)) },
                {
                    //TODO Show error message
                    loading.postValue(false)
                }
            )
        }
    }
}