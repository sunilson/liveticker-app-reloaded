package at.sunilson.liveticker.livetickercreation.presentation.livetickerCreation

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.Location
import at.sunilson.liveticker.livetickercreation.data.LivetickerCreationRepository
import at.sunilson.liveticker.presentation.baseClasses.Back
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

abstract class LivetickerCreationViewModel : BaseViewModel() {
    abstract val usePosition: MutableLiveData<Boolean>
    abstract val title: MutableLiveData<String>
    abstract val shortDescription: MutableLiveData<String>
    abstract val description: MutableLiveData<String>
    abstract val location: MutableLiveData<Location>

    abstract fun createLiveTicker(view: View? = null)
    abstract fun reset()
}

class LivetickerCreationViewModelImpl(private val repository: LivetickerCreationRepository) : LivetickerCreationViewModel() {
    override val usePosition: MutableLiveData<Boolean> = MutableLiveData()
    override val title: MutableLiveData<String> = MutableLiveData()
    override val shortDescription: MutableLiveData<String> = MutableLiveData()
    override val description: MutableLiveData<String> = MutableLiveData()
    override val location: MutableLiveData<Location> = MutableLiveData()

    init {
        usePosition.postValue(true)
    }

    override fun createLiveTicker(view: View?) {
        viewModelScope.launch {
            //val id = authenticationRepository.getCurrentUserNow()?.id ?: return@launch
            repository.createLiveticker(LiveTicker("", "", "", "", Date(), Date(), "", false, false))
            navigationEvents.postValue(Back)
        }
    }

    override fun reset() {
        title.value = null
        description.value = null
        shortDescription.value = null
        location.value = null
        usePosition.value = true
    }
}