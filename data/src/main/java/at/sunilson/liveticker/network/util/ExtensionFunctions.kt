package at.sunilson.liveticker.network.util

import at.sunilson.liveticker.core.models.ModelWithId
import at.sunilson.liveticker.firebasecore.ActionResult
import at.sunilson.liveticker.firebasecore.generateCompleteListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.suspendCancellableCoroutine

/**
 * Deletes a [DocumentReference] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitDelete(): ActionResult {
    return suspendCancellableCoroutine { cont ->
        delete().addOnCompleteListener(generateCompleteListener(cont))
    }
}

/**
 * Sets a [DocumentReference] to the given [data] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitSet(data: Any): ActionResult {
    return suspendCancellableCoroutine { cont ->
        set(data).addOnCompleteListener(generateCompleteListener(cont))
    }
}

/**
 * Adds to a [CollectionReference] the given [data] and waits for the callback to finish.
 */
suspend fun CollectionReference.awaitAdd(data: Any): ActionResult {
    return suspendCancellableCoroutine { cont ->
        add(data).addOnCompleteListener(generateCompleteListener(cont))
    }
}

fun <T : ModelWithId> Query.observe(clazz: Class<T>): ReceiveChannel<List<T>> {
    val channel = Channel<List<T>>()
    var listener: ListenerRegistration? = null

    listener = addSnapshotListener { querySnapshot, exception ->
        exception?.let {
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        channel.sendBlocking(querySnapshot?.map { document ->
            document.toObject(clazz).apply { id = document.id }
        } ?: return@addSnapshotListener)
    }

    return channel
}

fun <T : ModelWithId> DocumentReference.observe(clazz: Class<T>): ReceiveChannel<T> {
    val channel = Channel<T>()
    var listener: ListenerRegistration? = null

    listener = addSnapshotListener { documentSnapshot, exception ->
        exception?.let {
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        channel.sendBlocking(documentSnapshot?.toObject(clazz)?.apply {
            id = documentSnapshot.id
        } ?: return@addSnapshotListener)
    }

    return channel
}