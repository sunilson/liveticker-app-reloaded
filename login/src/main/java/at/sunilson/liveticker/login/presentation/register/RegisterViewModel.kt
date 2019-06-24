package at.sunilson.liveticker.login.presentation.register

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.login.domain.RegisterUseCase
import at.sunilson.liveticker.login.domain.models.LoginCredentials
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class RegisterViewModel : BaseViewModel() {
    abstract val userName: MutableLiveData<String>
    abstract val email: MutableLiveData<String>
    abstract val password: MutableLiveData<String>
    abstract val password2: MutableLiveData<String>

    abstract fun register(view: View? = null)
    abstract fun login(view: View? = null)

    object Login : NavigationEvent
    object Registered : NavigationEvent
}


class RegisterViewModelImpl(private val registerUseCase: RegisterUseCase) : RegisterViewModel() {

    override val userName: MutableLiveData<String> = MutableLiveData()
    override val email: MutableLiveData<String> = MutableLiveData()
    override val password: MutableLiveData<String> = MutableLiveData()
    override val password2: MutableLiveData<String> = MutableLiveData()

    override fun register(view: View?) {
        Timber.d("Starting register...")
        viewModelScope.launch {
            try {
                loading.postValue(true)
                registerUseCase(
                    LoginCredentials(
                        email.value ?: return@launch,
                        password.value ?: return@launch,
                        userName.value ?: return@launch
                    )
                )
                Timber.d("Register success!")
                navigationEvents.postValue(Registered)
            } catch (error: Throwable) {
                Timber.e(error, "Register failure!")
                errors.postValue(error.message)
            }

            loading.postValue(false)
        }
    }

    override fun login(view: View?) = navigationEvents.postValue(Login)
}