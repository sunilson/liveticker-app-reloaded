package at.sunilson.liveticker.livetickercreation.presentation.locationPickerDialog

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.models.Location
import at.sunilson.liveticker.location.AddressResolver
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import com.github.kittinunf.result.success
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class LocationPickerDialogViewModel : BaseViewModel() {
    abstract val selectedLocation: MutableLiveData<LatLng>

    abstract fun searchClicked(view: Any? = null)
    abstract fun tryFinish(view: Any? = null)

    object SearchClicked : NavigationEvent()
    data class LocationFound(val location: Location) : NavigationEvent()
}

class LocationPickerDialogViewModelImpl(private val addressResolver: AddressResolver) :
    LocationPickerDialogViewModel() {

    override val selectedLocation: MutableLiveData<LatLng> = MutableLiveData()

    override fun searchClicked(view: Any?) {
        navigationEvents.postValue(SearchClicked)
    }

    override fun tryFinish(view: Any?) {
        viewModelScope.launch {
            val selectedLocation = selectedLocation.value ?: return@launch
            val location = withContext(Dispatchers.Default) { addressResolver(selectedLocation) }
            location.success { navigationEvents.postValue(LocationFound(it)) }
        }
    }
}