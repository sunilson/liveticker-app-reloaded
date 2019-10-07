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
    abstract val searchQuery: MutableLiveData<String>
    abstract val search: MutableLiveData<Boolean>

    abstract fun searchClicked(view: Any? = null)
    abstract fun tryFinish(view: Any? = null)
    abstract fun searchUser()
}

sealed class LocationPickerNavigationEvent {
    data class LocationFound(val location: Location) : LocationPickerNavigationEvent()
    data class UserFound(val coordinates: Coordinates) : LocationPickerNavigationEvent()
    data class SearchResult(val coordinates: Coordinates) : LocationPickerNavigationEvent()
}

class LocationPickerDialogViewModelImpl(
    private val addressResolver: AddressResolver,
    private val locationFinder: LocationFinder
) :
    LocationPickerDialogViewModel() {

    override val selectedLocation: MutableLiveData<Coordinates> = MutableLiveData()
    override val search: MutableLiveData<Boolean> = MutableLiveData()
    override val searchQuery: MutableLiveData<String> = MutableLiveData()

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
        val search = search.value ?: false
        this.search.value = !search
    }

    override fun tryFinish(view: Any?) {
        viewModelScope.launch {
            if (searchQuery.value.isNullOrEmpty()) {
                loading.postValue(true)

                val selectedLocation = selectedLocation.value ?: return@launch
                val location = withContext(Dispatchers.Default) {
                    addressResolver.resolveLocationForCoordinates(selectedLocation)
                }

                location.fold(
                    { navigationEvents.postValue(LocationPickerNavigationEvent.LocationFound(it)) },
                    {
                        //TODO Show error message
                        loading.postValue(false)
                    }
                )
            } else {
                addressResolver.resolveLocationForAddress(searchQuery.value ?: return@launch).fold(
                    {
                        navigationEvents.postValue(LocationPickerNavigationEvent.SearchResult(it.coordinates))
                    },
                    {
                        //TODO Show error message
                    }
                )
                searchQuery.value = null
                search.value = false
                loading.postValue(false)
            }
        }
    }
}