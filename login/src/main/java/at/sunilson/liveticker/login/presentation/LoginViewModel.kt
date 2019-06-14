package at.sunilson.liveticker.login.presentation

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import kotlinx.coroutines.launch

abstract class LoginViewModel : BaseViewModel() {
    abstract val email: MutableLiveData<String>
    abstract val password: MutableLiveData<String>

    abstract suspend fun login(view: View? = null)
    abstract fun register(view: View? = null)

    object Register : NavigationEvent()
}

class LoginViewModelImpl(private val authentication: IAuthenticationRepository) : LoginViewModel() {

    override val email: MutableLiveData<String> = MutableLiveData()
    override val password: MutableLiveData<String> = MutableLiveData()

    override suspend fun login(view: View?) {
        //TODO Validate email/password

        viewModelScope.launch {
            try {
                loading.postValue(true)
                authentication.login(email.value ?: return@launch, password.value ?: return@launch)
            } catch (error: Throwable) {
                errors.postValue(error.message)
            }

            loading.postValue(false)
        }
    }

    override fun register(view: View?) = navigationEvents.postValue(Register)
}