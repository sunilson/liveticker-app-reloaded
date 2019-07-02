package at.sunilson.liveticker.presentation.dialogs.inputDialog

import android.view.View
import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent

abstract class InputDialogViewModel : BaseViewModel() {
    abstract val title: MutableLiveData<String>
    abstract val hint: MutableLiveData<String>
    abstract val confirm: MutableLiveData<String>
    abstract val inputText: MutableLiveData<String>

    abstract fun confirmClicked(view: View? = null)

    data class ConfirmClicked(val text: String?) : NavigationEvent
}

internal class InputDialogViewModelImpl : InputDialogViewModel() {
    override val confirm: MutableLiveData<String> = MutableLiveData()
    override val hint: MutableLiveData<String> = MutableLiveData()
    override val title: MutableLiveData<String> = MutableLiveData()
    override val inputText: MutableLiveData<String> = MutableLiveData()

    override fun confirmClicked(view: View?) {
        navigationEvents.postValue(ConfirmClicked(inputText.value))
    }
}