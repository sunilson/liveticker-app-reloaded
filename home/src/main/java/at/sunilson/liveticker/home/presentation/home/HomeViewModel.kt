package at.sunilson.liveticker.home.presentation.home

import android.view.View
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.ObservationResult
import at.sunilson.liveticker.home.data.HomeRepository
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import com.github.kittinunf.result.success
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class HomeViewModel : BaseViewModel() {
    abstract val livetickers: ObservableList<LiveTicker>

    abstract fun refresh()
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
    private val repository: HomeRepository,
    private val authenticationRepository: IAuthenticationRepository
) : HomeViewModel() {

    override val livetickers: ObservableList<LiveTicker> = ObservableArrayList()

    init {
        refresh()
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
            repository.deleteLiveticker(id).fold({
                //TODO
            }, {
                //TODOf
            })
        }
    }

    override fun refresh() {
        authenticationRepository.getCurrentUserNow().success {
            viewModelScope.launch(Dispatchers.Default) {
                for (result in repository.getLivetickers(it.id)) {
                    launch(Dispatchers.Main) {
                        when (result) {
                            is ObservationResult.Added<LiveTicker> -> livetickers.add(result.data)
                            is ObservationResult.Modified<LiveTicker> -> {
                                val index = livetickers.indexOfFirst { it.id == result.data.id }
                                livetickers[index] = result.data
                            }
                            is ObservationResult.Deleted<LiveTicker> -> {
                                val index = livetickers.indexOfFirst { it.id == result.data.id }
                                livetickers.removeAt(index)
                            }
                        }
                    }
                }
            }
        }
    }
}