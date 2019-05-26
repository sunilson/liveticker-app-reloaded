package at.sunilson.liveticker.home.presentation.home

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.network.IRemoteRepository
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import kotlinx.coroutines.launch

abstract class HomeViewModel : BaseViewModel() {
    abstract val livetickers: MutableLiveData<List<LiveTicker>>

    abstract fun refresh()
    abstract fun addLiveticker(view: View? = null)

    object AddLiveTicker : NavigationEvent()
}

class HomeViewModelImpl(
    private val repository: IRemoteRepository,
    private val authenticationRepository: IAuthenticationRepository
) : HomeViewModel() {

    override val livetickers: MutableLiveData<List<LiveTicker>> = MutableLiveData()

    init {
        refresh()
    }

    override fun addLiveticker(view: View?) {
        navigationEvents.postValue(AddLiveTicker)
    }

    override fun refresh() {
        val id = authenticationRepository.getCurrentUserNow()?.id ?: ""

        //TODO Error handling
        viewModelScope.launch {
            for (result in repository.getLivetickers(id)) {
                livetickers.postValue(result)
            }
        }
    }
}