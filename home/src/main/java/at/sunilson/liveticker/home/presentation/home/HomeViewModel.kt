package at.sunilson.liveticker.home.presentation.home

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.ObservationResult
import at.sunilson.liveticker.home.domain.DeleteLivetickerUsecase
import at.sunilson.liveticker.home.domain.GetLivetickersUseCase
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import at.sunilson.liveticker.presentation.removeWithId
import at.sunilson.liveticker.presentation.updateWithId
import com.github.kittinunf.result.coroutines.success
import com.github.kittinunf.result.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class HomeViewModel : BaseViewModel() {
    abstract val livetickers: ObservableList<LiveTicker>

    abstract suspend fun refresh()
    abstract fun addLiveticker(view: View? = null)
    abstract fun deleteLiveticker(id: String)
    abstract fun livetickerSelected(action: LivetickerSelectedAction)

    object AddLiveTicker : NavigationEvent
    data class ShareLiveticker(val liveTicker: LiveTicker) : NavigationEvent
    data class OpenLiveticker(val liveTicker: LiveTicker) : NavigationEvent
    data class DeleteLiveticker(val liveTicker: LiveTicker) : NavigationEvent
    object Login : NavigationEvent
}


class HomeViewModelImpl(
    private val authenticationRepository: IAuthenticationRepository,
    private val getLivetickersUseCase: GetLivetickersUseCase,
    private val deleteLivetickerUsecase: DeleteLivetickerUsecase
) : HomeViewModel() {

    override val livetickers: ObservableList<LiveTicker> = ObservableArrayList()

    init {
        viewModelScope.launch(Dispatchers.Default) { refresh() }
    }

    override fun addLiveticker(view: View?) {
        authenticationRepository.getCurrentUserNow().success {
            if (it.anonymous) {
                navigationEvents.postValue(Login)
            } else {
                navigationEvents.postValue(AddLiveTicker)
            }
        }
    }

    override fun livetickerSelected(action: LivetickerSelectedAction) {
        when (action) {
            is ShareClicked -> navigationEvents.postValue(ShareLiveticker(action.liveticker))
            is LivetickerClicked -> navigationEvents.postValue(OpenLiveticker(action.liveticker))
            is DeleteClicked -> navigationEvents.postValue(DeleteLiveticker(action.liveticker))
        }
    }

    override fun deleteLiveticker(id: String) {
        viewModelScope.launch {
            deleteLivetickerUsecase(id).fold(
                {},
                {
                    //TODO
                }
            )
        }
    }

    override suspend fun refresh() {
        val (user, authError) = authenticationRepository.getCurrentUserNow()

        if (user == null || authError != null) {
            //TODO
            return
        }

        getLivetickersUseCase(user.id).success {
            for (result in it) {
                result.fold(
                    { observationResult -> updateLivetickers(observationResult) },
                    {
                        //TODO
                    }
                )
            }
        }
    }

    private fun updateLivetickers(result: ObservationResult<LiveTicker>) {
        viewModelScope.launch(Dispatchers.Main) {
            when (result) {
                is ObservationResult.Added -> livetickers.add(result.data)
                is ObservationResult.Modified -> livetickers.updateWithId(result.data)
                is ObservationResult.Deleted -> livetickers.removeWithId(result.data)
            }
        }
    }
}