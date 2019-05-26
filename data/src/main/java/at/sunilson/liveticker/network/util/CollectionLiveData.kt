package at.sunilson.liveticker.network.util

import at.sunilson.liveticker.network.models.FirebaseResult
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot

/**
 * Livedata that subscribes to a [CollectionReference] and emits values as they come in. When going inactive, it will
 * unsubscribe from the reference.
 */
class CollectionLiveData<T>(
    private val clazz: Class<T>,
    private val reference: CollectionReference? = null,
    private val query: Query? = null
) :
    BaseLiveData<List<T>>() {

    override val listener: EventListener<QuerySnapshot> = EventListener { snapshot, error ->
        if (error != null) {
            postValue(FirebaseResult(null, error))
            return@EventListener
        } else if (snapshot != null) {
            postValue(FirebaseResult(snapshot.documentChanges.map { it.document.toObject(clazz) }, null))
        }
    }

    override fun onActive() {
        super.onActive()
        if (reference != null) {
            listenerRegistration = reference.addSnapshotListener(listener)
        } else if (query != null) {
            listenerRegistration = query.addSnapshotListener(listener)
        }
    }
}