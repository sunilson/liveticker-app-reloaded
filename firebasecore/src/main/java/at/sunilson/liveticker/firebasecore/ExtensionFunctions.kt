package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.core.models.ModelWithId
import com.github.kittinunf.result.coroutines.SuspendableResult
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
suspend fun DocumentReference.awaitDelete(): SuspendableResult<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        delete().addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Sets a [DocumentReference] to the given [data] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitSet(data: Any): SuspendableResult<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        set(data).addOnCompleteListener(generateCompletionListener(cont))
    }
}

/**
 * Adds to a [CollectionReference] the given [data] and waits for the callback to finish.
 */
suspend fun CollectionReference.awaitAdd(data: Any): SuspendableResult<String, Exception> {
    return suspendCancellableCoroutine { cont ->
        add(data).addOnCompleteListener(generateIdCompletionListener(cont))
    }
}

@ExperimentalCoroutinesApi
inline fun <reified T : ModelWithId> Query.observe(): ReceiveChannel<SuspendableResult<List<T>, FirebaseFirestoreException>> {
    var listener: ListenerRegistration? = null
    val channel = Channel<SuspendableResult<List<T>, FirebaseFirestoreException>>().apply {
        invokeOnClose {
            listener?.remove()
        }
    }

    listener = addSnapshotListener { querySnapshot, exception ->
        exception?.let {
            channel.sendBlocking(SuspendableResult.error(it))
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        channel.sendBlocking(
            SuspendableResult.Success(querySnapshot?.map { document ->
                document.toObject(T::class.java).apply { id = document.id }
            } ?: return@addSnapshotListener)
        )
    }

    return channel
}

@ExperimentalCoroutinesApi
inline fun <reified T : ModelWithId> Query.observeChanges(): ReceiveChannel<SuspendableResult<ObservationResult<T>, FirebaseFirestoreException>> {
    var listener: ListenerRegistration? = null
    val channel = Channel<SuspendableResult<ObservationResult<T>, FirebaseFirestoreException>>().apply {
        invokeOnClose {
            listener?.remove()
        }
    }

    listener = addSnapshotListener { querySnapshot, exception ->
        exception?.let {
            channel.sendBlocking(SuspendableResult.error(it))
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        querySnapshot?.documentChanges?.forEach {
            val data = it.document.toObject(T::class.java).apply { id = it.document.id }

            channel.sendBlocking(
                SuspendableResult.Success(
                    when (it.type) {
                        DocumentChange.Type.ADDED -> ObservationResult.Added(data)
                        DocumentChange.Type.REMOVED -> ObservationResult.Deleted(data)
                        DocumentChange.Type.MODIFIED -> ObservationResult.Modified(data)
                    }
                )
            )
        }
    }

    return channel
}

@ExperimentalCoroutinesApi
inline fun <reified T : ModelWithId> DocumentReference.observe(): ReceiveChannel<SuspendableResult<T, FirebaseFirestoreException>> {
    var listener: ListenerRegistration? = null
    val channel = Channel<SuspendableResult<T, FirebaseFirestoreException>>().apply {
        invokeOnClose {
            listener?.remove()
        }
    }

    listener = addSnapshotListener { documentSnapshot, exception ->
        exception?.let {
            channel.sendBlocking(SuspendableResult.error(it))
            listener?.remove()
            channel.close(it)
            return@addSnapshotListener
        }

        channel.sendBlocking(SuspendableResult.Success(
            documentSnapshot?.toObject(T::class.java)?.apply { id = documentSnapshot.id } ?: return@addSnapshotListener)
        )
    }

    return channel
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

//fun <T : Any> SuspendableResult.Companion.success(value: T): SuspendableResult<T, Nothing> = SuspendableResult.Success(value)
//fun <T: Any, E: Exception> SuspendableResult.Companion.success(value: T): SuspendableResult<T, E> = SuspendableResult.Success(value)

sealed class FirebaseOperationException(message: String? = null) : Exception(message)
class Cancelled : FirebaseOperationException()
class EmptyResult : FirebaseOperationException()
class Failed(message: String?) : FirebaseOperationException(message)