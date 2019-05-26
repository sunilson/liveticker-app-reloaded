package at.sunilson.liveticker.home.presentation.livetickerCreation

import android.view.View
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.network.IRemoteRepository
import at.sunilson.liveticker.presentation.baseClasses.Back
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import kotlinx.coroutines.launch
import java.util.*

abstract class LivetickerCreationViewModel : BaseViewModel() {
    abstract fun createLiveTicker(view: View? = null)
}

class LivetickerCreationViewModelImpl(
    private val repository: IRemoteRepository,
    private val authenticationRepository: IAuthenticationRepository
) : LivetickerCreationViewModel() {

    override fun createLiveTicker(view: View?) {
        viewModelScope.launch {
            //val id = authenticationRepository.getCurrentUserNow()?.id ?: return@launch
            repository.createLiveticker(LiveTicker("", "", "", "", Date(), Date(), "", false, false))
            navigationEvents.postValue(Back)
        }
    }
}