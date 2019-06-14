package at.sunilson.liveticker.login.presentation

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import kotlinx.coroutines.launch

abstract class RegisterViewModel : BaseViewModel() {
    abstract val email: MutableLiveData<String>
    abstract val password: MutableLiveData<String>
    abstract val password2: MutableLiveData<String>

    abstract suspend fun register(view: View? = null)
    abstract fun login(view: View? = null)

    object Login : NavigationEvent()
}


class RegisterViewModelImpl(private val authentication: IAuthenticationRepository) : RegisterViewModel() {

    override val email: MutableLiveData<String> = MutableLiveData()
    override val password: MutableLiveData<String> = MutableLiveData()
    override val password2: MutableLiveData<String> = MutableLiveData()

    override suspend fun register(view: View?) {
        //TODO Validate email/password

        viewModelScope.launch {
            try {
                loading.postValue(true)
                authentication.register(email.value ?: return@launch, password.value ?: return@launch)
            } catch (error: Throwable) {
                errors.postValue(error.message)
            }

            loading.postValue(false)
        }
    }

    override fun login(view: View?) = navigationEvents.postValue(Login)
}