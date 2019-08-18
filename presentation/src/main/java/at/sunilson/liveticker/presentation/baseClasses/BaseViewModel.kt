package at.sunilson.liveticker.presentation.baseClasses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import at.sunilson.liveticker.presentation.SingleLiveEvent

abstract class BaseViewModel<E> : ViewModel() {
    val loading: MutableLiveData<Boolean> = MutableLiveData()
    val navigationEvents: SingleLiveEvent<E> = SingleLiveEvent()
    val back: SingleLiveEvent<Any> = SingleLiveEvent()
    val toasts: SingleLiveEvent<Int> = SingleLiveEvent()

    fun back(any: Any? = null) {
        back.postValue(true)
    }
}