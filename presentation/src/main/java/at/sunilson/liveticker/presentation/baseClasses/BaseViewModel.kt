package at.sunilson.liveticker.presentation.baseClasses

import android.view.View
import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.sunilson.liveticker.presentation.SingleLiveEvent

interface NavigationEvent
object Back : NavigationEvent

abstract class BaseViewModel : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val navigationEvents: SingleLiveEvent<NavigationEvent> = SingleLiveEvent()
    val toasts: MutableLiveData<Int> = MutableLiveData()

    fun back(view: View? = null) {
        navigationEvents.postValue(Back)
    }
}