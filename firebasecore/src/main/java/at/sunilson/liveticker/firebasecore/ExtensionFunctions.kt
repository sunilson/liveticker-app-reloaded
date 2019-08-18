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
suspend inline fun <reified T : Any> DocumentReference.awaitGet(): SuspendableResult<T, FirebaseOperationException> {
    val snapshot: SuspendableResult<DocumentSnapshot, FirebaseOperationException> =
        suspendCancellableCoroutine { cont -> get().addOnCompleteListener(generateResultCompletionListener(cont)) }

    return SuspendableResult.of<T, FirebaseOperationException> {
        try {
            snapshot.get().toObject(T::class.java)!!
        } catch (error: Exception) {
            throw (FirebaseOperationException.TransformationFailed())
        }
    }
}

/**
 * Gets a collection or returns [EmptyResult]
 */
suspend inline fun <reified T : Any> Query.awaitGet(): SuspendableResult<List<T>, FirebaseOperationException> {
    val snapshot: SuspendableResult<QuerySnapshot, FirebaseOperationException> =
        suspendCancellableCoroutine { cont ->
            get().addOnCompleteListener(generateResultCompletionListener(cont))
        }

    return SuspendableResult.of<List<T>, FirebaseOperationException> {
        snapshot.get().documents.map {
            try {
                it.toObject(T::class.java)!!
            } catch (error: Exception) {
                throw (FirebaseOperationException.TransformationFailed())
            }
        }
    }
}


/**
 * Deletes a [DocumentReference] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitDelete(): SuspendableResult<Unit, FirebaseOperationException> {
    return suspendCancellableCoroutine { cont ->
        delete().addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Sets a [DocumentReference] to the given [data] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitSet(data: FirebaseEntity): SuspendableResult<Unit, FirebaseOperationException> {
    return suspendCancellableCoroutine { cont ->
        set(data).addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Adds to a [CollectionReference] the given [data] and waits for the callback to finish.
 */
suspend fun CollectionReference.awaitAdd(data: FirebaseEntity): SuspendableResult<String, FirebaseOperationException> {
    return suspendCancellableCoroutine { cont ->
        add(data).addOnCompleteListener(generateIdCompletionListener(cont))
    }
}

/**
 * Updates a document and waits for operation to finish
 */
suspend fun DocumentReference.awaitUpdate(map: Map<String, Any>): SuspendableResult<Unit, FirebaseOperationException> {
    return suspendCancellableCoroutine { cont ->
        update(map).addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Observe a collection and emit a List of all the data in the collection on each change
 */
inline fun <reified T : FirebaseEntity, R> Query.observe(crossinline map: (T) -> R): Flow<SuspendableResult<List<R>, FirebaseOperationException>> {
    return flowViaChannel { channel ->
        var listener: ListenerRegistration? = null

        listener = addSnapshotListener { querySnapshot, exception ->
            exception?.let {
                channel.sendBlocking(SuspendableResult.error(it.convert()))
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
inline fun <reified T : FirebaseEntity, R> Query.observeChanges(crossinline map: (T) -> R): Flow<SuspendableResult<List<ObservationResult<R>>, FirebaseOperationException>> {
    return flowViaChannel { channel ->
        var listener: ListenerRegistration? = null
        listener = addSnapshotListener { querySnapshot, exception ->
            exception?.let {
                channel.sendBlocking(SuspendableResult.error(it.convert()))
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
inline fun <reified T : FirebaseEntity, R : Any> DocumentReference.observe(crossinline map: (T) -> R): Flow<SuspendableResult<R, FirebaseOperationException>> {
    return flowViaChannel { channel ->
        var listener: ListenerRegistration? = null

        listener = addSnapshotListener { documentSnapshot, exception ->
            exception?.let {
                channel.sendBlocking(SuspendableResult.error(it.convert()))
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
        try {
            when {
                it.isCanceled -> cont.resume(SuspendableResult.error(FirebaseOperationException.Cancelled()))
                it.isSuccessful -> cont.resume(SuspendableResult.Success(Unit))
                else -> cont.resume(SuspendableResult.error(FirebaseOperationException.Failed("")))
            }
        } catch (error: Exception) {
            when (it.exception) {
                is FirebaseFirestoreException -> cont.resume(SuspendableResult.error((it.exception as FirebaseFirestoreException).convert()))
                else -> cont.resume(SuspendableResult.error(FirebaseOperationException.Failed(error.message)))
            }
        }
    }
}

fun <T> generateIdCompletionListener(cont: Continuation<SuspendableResult<String, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {
        try {
            val id = (it.result as? DocumentReference)?.id
            when {
                it.isCanceled -> cont.resume(SuspendableResult.error(FirebaseOperationException.Cancelled()))
                id == null -> cont.resume(SuspendableResult.error(FirebaseOperationException.EmptyResult()))
                it.isSuccessful -> cont.resume(SuspendableResult.Success(id))
                else -> cont.resume(SuspendableResult.error(FirebaseOperationException.Failed("")))
            }
        } catch (error: Exception) {
            when (it.exception) {
                is FirebaseFirestoreException -> cont.resume(SuspendableResult.error((it.exception as FirebaseFirestoreException).convert()))
                else -> cont.resume(SuspendableResult.error(FirebaseOperationException.Failed(error.message)))
            }
        }
    }
}

fun <T : Any> generateResultCompletionListener(cont: Continuation<SuspendableResult<T, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {
        try {
            val result = it.result
            when {
                it.isCanceled -> cont.resume(SuspendableResult.error(FirebaseOperationException.Cancelled()))
                result == null -> cont.resume(SuspendableResult.error(FirebaseOperationException.EmptyResult()))
                it.isSuccessful -> cont.resume(SuspendableResult.Success(result))
                else -> cont.resume(SuspendableResult.error(FirebaseOperationException.Failed("")))
            }
        } catch (error: Exception) {
            when (it.exception) {
                is FirebaseFirestoreException -> cont.resume(SuspendableResult.error((it.exception as FirebaseFirestoreException).convert()))
                else -> cont.resume(SuspendableResult.error(FirebaseOperationException.Failed(error.message)))
            }
        }
    }
}

fun FirebaseFirestoreException.convert(): FirebaseOperationException {
    return when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> FirebaseOperationException.PermissionDenied()
        FirebaseFirestoreException.Code.OK -> TODO()
        FirebaseFirestoreException.Code.CANCELLED -> TODO()
        FirebaseFirestoreException.Code.UNKNOWN -> TODO()
        FirebaseFirestoreException.Code.INVALID_ARGUMENT -> TODO()
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED -> TODO()
        FirebaseFirestoreException.Code.NOT_FOUND -> TODO()
        FirebaseFirestoreException.Code.ALREADY_EXISTS -> TODO()
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED -> TODO()
        FirebaseFirestoreException.Code.FAILED_PRECONDITION -> TODO()
        FirebaseFirestoreException.Code.ABORTED -> TODO()
        FirebaseFirestoreException.Code.OUT_OF_RANGE -> TODO()
        FirebaseFirestoreException.Code.UNIMPLEMENTED -> TODO()
        FirebaseFirestoreException.Code.INTERNAL -> TODO()
        FirebaseFirestoreException.Code.UNAVAILABLE -> TODO()
        FirebaseFirestoreException.Code.DATA_LOSS -> TODO()
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> TODO()
    }
}

sealed class FirebaseOperationException(message: String? = null) : Exception(message) {
    class Cancelled : FirebaseOperationException()
    class EmptyResult : FirebaseOperationException()
    class PermissionDenied : FirebaseOperationException()
    class TransformationFailed : FirebaseOperationException()
    class Failed(message: String?) : FirebaseOperationException(message)
}
