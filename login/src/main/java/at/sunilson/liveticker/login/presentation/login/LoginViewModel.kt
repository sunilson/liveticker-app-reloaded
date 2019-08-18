package at.sunilson.liveticker.login.presentation.login

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.login.R
import at.sunilson.liveticker.login.domain.EmailInvalid
import at.sunilson.liveticker.login.domain.LoginUsecase
import at.sunilson.liveticker.login.domain.PasswordInvalid
import at.sunilson.liveticker.login.domain.models.LoginCredentials
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import kotlinx.coroutines.launch

abstract class LoginViewModel : BaseViewModel<LoginNavigationEvent>() {
    abstract val email: MutableLiveData<String>
    abstract val password: MutableLiveData<String>

    abstract fun login(view: View? = null)
    abstract fun register(view: View? = null)
}

sealed class LoginNavigationEvent () {
    object Register : LoginNavigationEvent()
    object LoggedIn : LoginNavigationEvent()
}

class LoginViewModelImpl(private val loginUsecase: LoginUsecase) : LoginViewModel() {

    override val email: MutableLiveData<String> = MutableLiveData()
    override val password: MutableLiveData<String> = MutableLiveData()

    override fun login(view: View?) {
        viewModelScope.launch {
            loading.postValue(true)
            loginUsecase(LoginCredentials(email.value, password.value)).fold(
                { navigationEvents.postValue(LoginNavigationEvent.LoggedIn) },
                {
                    loading.postValue(false)
                    when (it) {
                        is EmailInvalid -> toasts.postValue(R.string.email_invalid)
                        is PasswordInvalid -> toasts.postValue(R.string.password_invalid)
                        else -> toasts.postValue(R.string.authentication_error)
                    }
                }
            )
        }
    }

    override fun register(view: View?) = navigationEvents.postValue(LoginNavigationEvent.Register)
}