package at.sunilson.liveticker

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.models.User
import at.sunilson.liveticker.presentation.MainViewModel
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
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

        listOf(
            Color.parseColor("#7986CB") to Color.parseColor("#D50000"),
            Color.parseColor("#E91E63") to Color.parseColor("#7B1FA2"),
            Color.parseColor("#00BCD4") to Color.parseColor("#C0CA33"),
            Color.parseColor("#FBC02D") to Color.parseColor("#795548"),
            Color.parseColor("#FF5722") to Color.parseColor("#9575CD"),
            Color.parseColor("#AD1457") to Color.parseColor("#EA80FC"),
            Color.parseColor("#26A69A") to Color.parseColor("#FF6F00"),
            Color.parseColor("#8BC34A") to Color.parseColor("#FF8F00")
        )


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