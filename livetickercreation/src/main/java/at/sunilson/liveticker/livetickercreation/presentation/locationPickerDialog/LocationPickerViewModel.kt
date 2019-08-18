package at.sunilson.liveticker.livetickercreation.presentation.locationPickerDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.models.Coordinates
import at.sunilson.liveticker.core.models.Location
import at.sunilson.liveticker.location.AddressResolver
import at.sunilson.liveticker.location.LocationFinder
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class LocationPickerDialogViewModel : BaseViewModel<LocationPickerNavigationEvent>() {
    abstract val selectedLocation: MutableLiveData<Coordinates>

    abstract fun searchClicked(view: Any? = null)
    abstract fun tryFinish(view: Any? = null)
    abstract fun searchUser()
}

sealed class LocationPickerNavigationEvent {
    object SearchClicked : LocationPickerNavigationEvent()
    data class LocationFound(val location: Location) : LocationPickerNavigationEvent()
    data class UserFound(val coordinates: Coordinates) : LocationPickerNavigationEvent()
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
                navigationEvents.postValue(
                    LocationPickerNavigationEvent.UserFound(
                        Coordinates(
                            result.latitude,
                            result.longitude
                        )
                    )
                )
            }, {
                //TODO Show error message
            })
        }
    }

    override fun searchClicked(view: Any?) {
        navigationEvents.postValue(LocationPickerNavigationEvent.SearchClicked)
    }

    override fun tryFinish(view: Any?) {
        loading.postValue(true)

        viewModelScope.launch {
            val selectedLocation = selectedLocation.value ?: return@launch
            val location = withContext(Dispatchers.Default) { addressResolver(selectedLocation) }

            location.fold(
                { navigationEvents.postValue(LocationPickerNavigationEvent.LocationFound(it)) },
                {
                    //TODO Show error message
                    loading.postValue(false)
                }
            )
        }
    }
}