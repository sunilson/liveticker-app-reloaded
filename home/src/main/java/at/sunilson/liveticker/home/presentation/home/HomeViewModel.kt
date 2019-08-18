package at.sunilson.liveticker.home.presentation.home

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.getOrNull
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.utils.Do
import at.sunilson.liveticker.home.domain.DeleteLivetickerUsecase
import at.sunilson.liveticker.sharing.domain.GetEditUrlUseCase
import at.sunilson.liveticker.home.domain.GetLivetickersUseCase
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.handleObservationResults
import com.github.kittinunf.result.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class HomeViewModel : BaseViewModel<HomeNavigationEvent>() {
    abstract val livetickers: MutableLiveData<List<LiveTicker>>
    abstract fun refresh()
    abstract fun addLiveticker(view: View? = null)
    abstract fun deleteLiveticker(id: String)
    abstract fun livetickerSelected(action: LivetickerSelectedAction)
}

sealed class HomeNavigationEvent() {
    object AddLiveTicker : HomeNavigationEvent()
    data class ShareLiveticker(val viewUrl: String, val editUrl: String?) : HomeNavigationEvent()
    data class OpenLiveticker(val liveTicker: LiveTicker) : HomeNavigationEvent()
    data class DeleteLiveticker(val liveTicker: LiveTicker) : HomeNavigationEvent()
    object Login : HomeNavigationEvent()
}


class HomeViewModelImpl(
    private val authenticationRepository: IAuthenticationRepository,
    private val getLivetickersUseCase: GetLivetickersUseCase,
    private val deleteLivetickerUsecase: DeleteLivetickerUsecase,
    private val getEditUrlUseCase: GetEditUrlUseCase
) : HomeViewModel() {

    override val livetickers: MutableLiveData<List<LiveTicker>> = MutableLiveData()

    override fun addLiveticker(view: View?) {
        authenticationRepository.getCurrentUserNow().success {
            if (it.anonymous) {
                navigationEvents.postValue(HomeNavigationEvent.Login)
            } else {
                navigationEvents.postValue(HomeNavigationEvent.AddLiveTicker)
            }
        }
    }

    override fun livetickerSelected(action: LivetickerSelectedAction) {
        Do exhaustive when (action) {
            is ShareClicked -> {
                viewModelScope.launch {
                    navigationEvents.postValue(
                        HomeNavigationEvent.ShareLiveticker(
                            action.liveticker.sharingUrl,
                            getEditUrlUseCase(action.liveticker.id).getOrNull()
                        )
                    )
                }
            }
            is LivetickerClicked -> navigationEvents.postValue(HomeNavigationEvent.OpenLiveticker(action.liveticker))
            is DeleteClicked -> navigationEvents.postValue(HomeNavigationEvent.DeleteLiveticker(action.liveticker))
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

    override fun refresh() {
        viewModelScope.launch {
            val (user, authError) = authenticationRepository.getCurrentUserNow()

            if (user == null || authError != null) {
                livetickers.value = listOf()
                return@launch
            }

            getLivetickersUseCase(user.id).collect { result ->
                result.fold(
                    { launch(Dispatchers.Main) { livetickers.value = it } },
                    {
                        Timber.e(it, "Error loading livetickers")
                        //TODO
                    }
                )
            }
        }
    }
}