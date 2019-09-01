package at.sunilson.liveticker

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.models.User
import at.sunilson.liveticker.presentation.MainViewModel
import kotlinx.coroutines.launch
import timber.log.Timber

internal class MainViewModelImpl(private val authenticationRepository: IAuthenticationRepository) :
    MainViewModel() {
    override val currentUser: MutableLiveData<User?> = MutableLiveData()

    init {
        viewModelScope.launch {
            for (user in authenticationRepository.observeAuthentication()) {
                currentUser.postValue(user)
            }
        }
    }

    override fun anonymousLogin() {
        viewModelScope.launch {
            authenticationRepository.anonymousLogin().fold({
                Timber.d("Anonymous login success!")
            }, {
                Timber.d("Anonymous login failed: ${it.message}")
            })
        }
    }

    override fun logout() {
        authenticationRepository.logout()
    }
}