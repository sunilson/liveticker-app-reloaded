package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.core.models.ModelWithId
import com.github.kittinunf.result.Result
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * Deletes a [DocumentReference] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitDelete(): Result<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        delete().addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Sets a [DocumentReference] to the given [data] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitSet(data: Any): Result<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        set(data).addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Adds to a [CollectionReference] the given [data] and waits for the callback to finish.
 */
suspend fun CollectionReference.awaitAdd(data: Any): Result<String, Exception> {
    return suspendCancellableCoroutine { cont ->
        add(data).addOnCompleteListener(generateIdCompletionListener(cont))
    }
}

@ExperimentalCoroutinesApi
inline fun <reified T : ModelWithId> Query.observe(): ReceiveChannel<List<T>> {
    var listener: ListenerRegistration? = null
    val channel = Channel<List<T>>().apply {
        invokeOnClose {
            listener?.remove()
        }
    }

    listener = addSnapshotListener { querySnapshot, exception ->
        exception?.let {
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        channel.sendBlocking(querySnapshot?.map { document ->
            document.toObject(T::class.java).apply { id = document.id }
        } ?: return@addSnapshotListener)
    }

    return channel
}

@ExperimentalCoroutinesApi
inline fun <reified T : ModelWithId> Query.observeChanges(): ReceiveChannel<ObservationResult<T>> {
    var listener: ListenerRegistration? = null
    val channel = Channel<ObservationResult<T>>().apply {
        invokeOnClose {
            listener?.remove()
        }
    }

    listener = addSnapshotListener { querySnapshot, exception ->
        exception?.let {
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        querySnapshot?.documentChanges?.forEach {
            val data = it.document.toObject(T::class.java).apply { id = it.document.id }

            channel.sendBlocking(
                when (it.type) {
                    DocumentChange.Type.ADDED -> ObservationResult.Added(data)
                    DocumentChange.Type.REMOVED -> ObservationResult.Deleted(data)
                    DocumentChange.Type.MODIFIED -> ObservationResult.Modified(data)
                }
            )
        }
    }

    return channel
}

@ExperimentalCoroutinesApi
inline fun <reified T : ModelWithId> DocumentReference.observe(): ReceiveChannel<T> {
    var listener: ListenerRegistration? = null
    val channel = Channel<T>().apply {
        invokeOnClose {
            listener?.remove()
        }
    }

    listener = addSnapshotListener { documentSnapshot, exception ->
        exception?.let {
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        channel.sendBlocking(documentSnapshot?.toObject(T::class.java)?.apply {
            id = documentSnapshot.id
        } ?: return@addSnapshotListener)
    }

    return channel
}

fun <T> generateCompletionListener(cont: Continuation<Result<Unit, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {
        when {
            it.exception != null -> cont.resume(Result.error(Failed(it.exception?.message)))
            it.isCanceled -> cont.resume(Result.error(Cancelled()))
            it.isSuccessful -> cont.resume(Result.success(Unit))
            else -> cont.resume(Result.error(Failed("")))
        }
    }
}

fun <T> generateIdCompletionListener(cont: Continuation<Result<String, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {

        val id = (it.result as? DocumentReference)?.id

        when {
            it.exception != null -> cont.resume(Result.error(Failed(it.exception?.message)))
            it.isCanceled -> cont.resume(Result.error(Cancelled()))
            id == null -> cont.resume(Result.error(EmptyResult()))
            it.isSuccessful -> cont.resume(Result.success(id))
            else -> cont.resume(Result.error(Failed("")))
        }
    }
}

fun <T : Any> generateResultCompletionListener(cont: Continuation<Result<T, FirebaseOperationException>>): OnCompleteListener<T> {
    return OnCompleteListener {
        val result = it.result

        when {
            it.exception != null -> cont.resume(Result.error(Failed(it.exception?.message)))
            it.isCanceled -> cont.resume(Result.error(Cancelled()))
            result == null -> cont.resume(Result.error(EmptyResult()))
            it.isSuccessful -> cont.resume(Result.success(result))
            else -> cont.resume(Result.error(Failed("")))
        }
    }
}


sealed class FirebaseOperationException(message: String? = null) : Exception(message)
class Cancelled : FirebaseOperationException()
class EmptyResult : FirebaseOperationException()
class Failed(message: String?) : FirebaseOperationException(message)