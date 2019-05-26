package at.sunilson.liveticker.network.util

import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.network.models.FirebaseResult
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ListenerRegistration

abstract class BaseLiveData <T> : MutableLiveData<FirebaseResult<T>>() {

    protected var listenerRegistration: ListenerRegistration? = null
    protected abstract val listener: EventListener<*>

    override fun onInactive() {
        super.onInactive()
    }
}