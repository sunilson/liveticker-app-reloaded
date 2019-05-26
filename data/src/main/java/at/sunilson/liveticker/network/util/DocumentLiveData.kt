package at.sunilson.liveticker.network.util

import at.sunilson.liveticker.network.models.FirebaseResult
import com.google.firebase.firestore.*

/**
 * Livedata that subscribes to a [DocumentReference] and emits values as they come in. When going inactive, it will
 * unsubscribe from the reference.
 */
class DocumentLiveData<T>(private val reference: DocumentReference, private val clazz: Class<T>) :
    BaseLiveData<T>() {

    override val listener: EventListener<DocumentSnapshot> = EventListener { snapshot, error ->
        if (error != null) {
            postValue(FirebaseResult(null, error))
            return@EventListener
        } else if (snapshot != null) {
            postValue(FirebaseResult(snapshot.toObject(clazz), null))
        }
    }

    override fun onActive() {
        super.onActive()
        listenerRegistration = reference.addSnapshotListener(listener)
    }
}