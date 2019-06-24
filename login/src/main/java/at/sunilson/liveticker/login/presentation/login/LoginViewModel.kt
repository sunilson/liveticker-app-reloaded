package at.sunilson.liveticker.login.presentation.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.login.domain.LoginUsecase
import at.sunilson.liveticker.login.domain.models.LoginCredentials
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import kotlinx.coroutines.launch

abstract class LoginViewModel : BaseViewModel() {
    abstract val email: MutableLiveData<String>
    abstract val password: MutableLiveData<String>

    abstract fun login(view: View? = null)
    abstract fun register(view: View? = null)

    object Register : NavigationEvent
    object LoggedIn : NavigationEvent
}

class LoginViewModelImpl(private val loginUsecase: LoginUsecase) : LoginViewModel() {

    override val email: MutableLiveData<String> = MutableLiveData()
    override val password: MutableLiveData<String> = MutableLiveData()

    override fun login(view: View?) {
        viewModelScope.launch {
            try {
                loading.postValue(true)
                loginUsecase(LoginCredentials(email.value ?: return@launch, password.value ?: return@launch))
                navigationEvents.postValue(LoggedIn)
            } catch (error: Throwable) {
                errors.postValue(error.message)
            }

            loading.postValue(false)
        }
    }

    override fun register(view: View?) = navigationEvents.postValue(Register)
}