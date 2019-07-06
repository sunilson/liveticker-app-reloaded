package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.core.ObservationResult
import at.sunilson.liveticker.firebasecore.models.FirebaseEntity
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * Gets a document or returns [EmptyResult]
 */
suspend inline fun <reified T : FirebaseEntity> DocumentReference.awaitGet(): SuspendableResult<T, Exception> {
    val snapshot: SuspendableResult<DocumentSnapshot, FirebaseOperationException> =
        suspendCancellableCoroutine { cont ->
            get().addOnCompleteListener(generateResultCompletionListener(cont))
        }

    return SuspendableResult.of {
        try {
            snapshot.get().toObject(T::class.java)!!
        } catch (error: Throwable) {
            throw EmptyResult()
        }
    }
}

/**
 * Gets a collection or returns [EmptyResult]
 */
suspend inline fun <reified T : FirebaseEntity> Query.awaitGet(): SuspendableResult<List<T>, Exception> {
    val snapshot: SuspendableResult<QuerySnapshot, FirebaseOperationException> =
        suspendCancellableCoroutine { cont ->
            get().addOnCompleteListener(generateResultCompletionListener(cont))
        }

    return SuspendableResult.of {
        try {
            snapshot.get().documents.map { it.toObject(T::class.java)!! }
        } catch (error: Throwable) {
            throw EmptyResult()
        }
    }
}


/**
 * Deletes a [DocumentReference] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitDelete(): SuspendableResult<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        delete().addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Sets a [DocumentReference] to the given [data] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitSet(data: FirebaseEntity): SuspendableResult<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        set(data).addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Adds to a [CollectionReference] the given [data] and waits for the callback to finish.
 */
suspend fun CollectionReference.awaitAdd(data: FirebaseEntity): SuspendableResult<String, Exception> {
    return suspendCancellableCoroutine { cont ->
        add(data).addOnCompleteListener(generateIdCompletionListener(cont))
    }
}

/**
 * Updates a document and waits for operation to finish
 */
suspend fun DocumentReference.awaitUpdate(map: Map<String, Any>): SuspendableResult<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        update(map).addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Observe a collection and emit a List of all the data in the collection on each change
 */
inline fun <reified T : FirebaseEntity, R> Query.observe(crossinline map: (T) -> R): Flow<SuspendableResult<List<R>, FirebaseFirestoreException>> {
    return flowViaChannel { channel ->
        var listener: ListenerRegistration? = null

        listener = addSnapshotListener { querySnapshot, exception ->
            exception?.let {
                channel.sendBlocking(SuspendableResult.error(it))
                listener?.remove()
                channel.close(it)
                return@addSnapshotListener
            }

            channel.sendBlocking(
                SuspendableResult.Success(querySnapshot?.map { document ->
                    map(document.toObject(T::class.java).apply { id = document.id })
                } ?: return@addSnapshotListener)
            )
        }

        channel.invokeOnClose { listener.remove() }
    }
}

/**
 * Observe a collection and emit a List of all changes to the data in the collection
 */
inline fun <reified T : FirebaseEntity, R> Query.observeChanges(crossinline map: (T) -> R): Flow<SuspendableResult<List<ObservationResult<R>>, FirebaseFirestoreException>> {
    return flowViaChannel { channel ->
        var listener: ListenerRegistration? = null
        listener = addSnapshotListener { querySnapshot, exception ->
            exception?.let {
                channel.sendBlocking(SuspendableResult.error(it))
                listener?.remove()
                channel.close(it)
                return@addSnapshotListener
            }

            val changes = querySnapshot?.documentChanges?.map {
                val data = it.document.toObject(T::class.java).apply { id = it.document.id }
                when (it.type) {
                    DocumentChange.Type.ADDED -> ObservationResult.Added(map(data))
                    DocumentChange.Type.REMOVED -> ObservationResult.Deleted(map(data))
                    DocumentChange.Type.MODIFIED -> ObservationResult.Modified(map(data))
                }
            }

            channel.sendBlocking(SuspendableResult.Success(changes ?: return@addSnapshotListener))
        }

        channel.invokeOnClose { listener.remove() }
    }
}

/**
 * Observe a document and emit the data in the collection on each change
 */
inline fun <reified T : FirebaseEntity, R : Any> DocumentReference.observe(crossinline map: (T) -> R): Flow<SuspendableResult<R, FirebaseFirestoreException>> {
    return flowViaChannel { channel ->
        var listener: ListenerRegistration? = null

        listener = addSnapshotListener { documentSnapshot, exception ->
            exception?.let {
                channel.sendBlocking(SuspendableResult.error(it))
                listener?.remove()
                channel.close(it)
                return@addSnapshotListener
            }

            val obj = documentSnapshot?.toObject(T::class.java)?.apply { id = documentSnapshot.id }
                ?: return@addSnapshotListener
            channel.sendBlocking(SuspendableResult.Success(map(obj)))
        }

        channel.invokeOnClose { listener.remove() }
    }
}

fun <T> generateCompletionListener(cont: Continuation<SuspendableResult<Unit, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {
        when {
            it.exception != null -> cont.resume(SuspendableResult.error(Failed(it.exception?.message)))
            it.isCanceled -> cont.resume(SuspendableResult.error(Cancelled()))
            it.isSuccessful -> cont.resume(SuspendableResult.Success(Unit))
            else -> cont.resume(SuspendableResult.error(Failed("")))
        }
    }
}

fun <T> generateIdCompletionListener(cont: Continuation<SuspendableResult<String, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {

        val id = (it.result as? DocumentReference)?.id

        when {
            it.exception != null -> cont.resume(SuspendableResult.error(Failed(it.exception?.message)))
            it.isCanceled -> cont.resume(SuspendableResult.error(Cancelled()))
            id == null -> cont.resume(SuspendableResult.error(EmptyResult()))
            it.isSuccessful -> cont.resume(SuspendableResult.Success(id))
            else -> cont.resume(SuspendableResult.error(Failed("")))
        }
    }
}

fun <T : Any> generateResultCompletionListener(cont: Continuation<SuspendableResult<T, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {
        val result = it.result

        when {
            it.exception != null -> cont.resume(SuspendableResult.error(Failed(it.exception?.message)))
            it.isCanceled -> cont.resume(SuspendableResult.error(Cancelled()))
            result == null -> cont.resume(SuspendableResult.error(EmptyResult()))
            it.isSuccessful -> cont.resume(SuspendableResult.Success(result))
            else -> cont.resume(SuspendableResult.error(Failed("")))
        }
    }
}

sealed class FirebaseOperationException(message: String? = null) : Exception(message)
class Cancelled : FirebaseOperationException()
class EmptyResult : FirebaseOperationException()
class Failed(message: String?) : FirebaseOperationException(message)