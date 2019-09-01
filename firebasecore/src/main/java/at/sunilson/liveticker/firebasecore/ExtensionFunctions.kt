package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.core.ObservationResult
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowViaChannel
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

abstract class FirebaseEntity(@Exclude var id: String, @ServerTimestamp val timestamp: Date? = null)

/**
 * Gets a document or returns [EmptyResult]
 */
suspend inline fun <reified T : Any> DocumentReference.awaitGet(): SuspendableResult<T, FirebaseOperationException> {
    val snapshot: SuspendableResult<DocumentSnapshot, FirebaseOperationException> =
        suspendCancellableCoroutine { cont ->
            get().addOnCompleteListener(
                generateResultCompletionListener(cont)
            )
        }

    return SuspendableResult.of {
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

    return SuspendableResult.of {
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
 * Commits the batch and waits for that operation to finish
 */
suspend fun WriteBatch.awaitCommit(): SuspendableResult<Unit, FirebaseOperationException> {
    return suspendCancellableCoroutine { cont ->
        commit().addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Observe a collection and emit a List of all the data in the collection on each change
 */
@ExperimentalCoroutinesApi
inline fun <reified T : FirebaseEntity, R> Query.observe(crossinline map: (T) -> R) =
    callbackFlow<SuspendableResult<List<R>, FirebaseOperationException>> {
        val listener = addSnapshotListener { querySnapshot, exception ->
            exception?.let {
                sendBlocking(SuspendableResult.error(it.convert()))
                close(it)
                return@addSnapshotListener
            }

            sendBlocking(
                SuspendableResult.Success(querySnapshot?.map { document ->
                    map(document.toObject(T::class.java).apply { id = document.id })
                } ?: return@addSnapshotListener)
            )
        }
        awaitClose { listener.remove() }
    }

/**
 * Observe a collection and emit a List of all changes to the data in the collection
 */
@ExperimentalCoroutinesApi
inline fun <reified T : FirebaseEntity, R> Query.observeChanges(crossinline map: (T) -> R) =
    callbackFlow<SuspendableResult<List<ObservationResult<R>>, FirebaseOperationException>> {
        val listener = addSnapshotListener { querySnapshot, exception ->
            exception?.let {
                sendBlocking(SuspendableResult.error(it.convert()))
                close(it)
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

            sendBlocking(SuspendableResult.Success(changes ?: return@addSnapshotListener))
        }

        awaitClose { listener.remove() }
    }

/**
 * Observe a document and emit the data in the collection on each change
 */
@ExperimentalCoroutinesApi
inline fun <reified T : FirebaseEntity, R : Any> DocumentReference.observe(crossinline map: (T) -> R) =
    callbackFlow<SuspendableResult<R, FirebaseOperationException>> {
        val listener = addSnapshotListener { documentSnapshot, exception ->
            exception?.let {
                sendBlocking(SuspendableResult.error(it.convert()))
                close(it)
                return@addSnapshotListener
            }

            val obj = documentSnapshot?.toObject(T::class.java)?.apply { id = documentSnapshot.id }
                ?: return@addSnapshotListener
            sendBlocking(SuspendableResult.Success(map(obj)))
        }

        awaitClose { listener.remove() }
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

/**
 * Converts a [FirebaseFirestoreException] to a [FirebaseOperationException] which is used throughout the app
 */
fun FirebaseFirestoreException.convert(): FirebaseOperationException {
    return when (code) {
        FirebaseFirestoreException.Code.PERMISSION_DENIED -> FirebaseOperationException.PermissionDenied()
        FirebaseFirestoreException.Code.UNAUTHENTICATED -> FirebaseOperationException.AuthenticationFailed()
        FirebaseFirestoreException.Code.CANCELLED -> FirebaseOperationException.Cancelled()
        FirebaseFirestoreException.Code.OK,
        FirebaseFirestoreException.Code.UNKNOWN,
        FirebaseFirestoreException.Code.INVALID_ARGUMENT,
        FirebaseFirestoreException.Code.DEADLINE_EXCEEDED,
        FirebaseFirestoreException.Code.NOT_FOUND,
        FirebaseFirestoreException.Code.ALREADY_EXISTS,
        FirebaseFirestoreException.Code.RESOURCE_EXHAUSTED,
        FirebaseFirestoreException.Code.FAILED_PRECONDITION,
        FirebaseFirestoreException.Code.ABORTED,
        FirebaseFirestoreException.Code.OUT_OF_RANGE,
        FirebaseFirestoreException.Code.UNIMPLEMENTED,
        FirebaseFirestoreException.Code.INTERNAL,
        FirebaseFirestoreException.Code.UNAVAILABLE,
        FirebaseFirestoreException.Code.DATA_LOSS -> FirebaseOperationException.Unknown(this)
    }
}

/**
 * Mapping of firebase errors to errors that are displayed in the app
 */
sealed class FirebaseOperationException(message: String? = null) : Exception(message) {
    class Cancelled : FirebaseOperationException()
    class EmptyResult : FirebaseOperationException()
    class PermissionDenied : FirebaseOperationException()
    class TransformationFailed : FirebaseOperationException()
    class AuthenticationFailed : FirebaseOperationException()
    class Failed(message: String?) : FirebaseOperationException(message)
    class Unknown(exception: FirebaseFirestoreException) : FirebaseOperationException()
}
