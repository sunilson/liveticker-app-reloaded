package at.sunilson.liveticker.presentation

import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.core.models.User
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel

abstract class MainViewModel : BaseViewModel<Any>() {
    abstract val currentUser: MutableLiveData<User?>
    abstract fun anonymousLogin()
    abstract fun logout()
}