package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.core.models.ModelWithId
import com.github.kittinunf.result.Result
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

/**
 * Deletes a [DocumentReference] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitDelete(): Result<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        delete().addOnCompleteListener(generateAddCompletionListener(cont))
    }
}

suspend inline fun <reified T : Any> DocumentReference.awaitDeleteResult(data: Any): Result<T, Exception> {
    return suspendCancellableCoroutine { cont ->
        delete().addOnCompleteListener(generateAddResultCompletionListener(cont))
    }
}

/**
 * Sets a [DocumentReference] to the given [data] and waits for the callback to finish.
 */
suspend fun DocumentReference.awaitSet(data: Any): Result<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        set(data).addOnCompleteListener(generateAddCompletionListener(cont))
    }
}

suspend inline fun <reified T : Any> DocumentReference.awaitSetResult(data: Any): Result<T, Exception> {
    return suspendCancellableCoroutine { cont ->
        set(data).addOnCompleteListener(generateAddResultCompletionListener(cont))
    }
}

/**
 * Adds to a [CollectionReference] the given [data] and waits for the callback to finish.
 */
suspend fun CollectionReference.awaitAdd(data: Any): Result<Unit, Exception> {
    return suspendCancellableCoroutine { cont ->
        add(data).addOnCompleteListener(generateAddCompletionListener(cont))
    }
}

suspend inline fun <reified T : Any> CollectionReference.awaitAddResult(data: Any): Result<T, Exception> {
    return suspendCancellableCoroutine { cont ->
        add(data).addOnCompleteListener(generateAddResultCompletionListener(cont))
    }
}

inline fun <reified T : ModelWithId> Query.observe(): ReceiveChannel<List<T>> {
    val channel = Channel<List<T>>()
    var listener: ListenerRegistration? = null

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

fun <T> generateAddCompletionListener(cont: Continuation<Result<Unit, Exception>>): OnCompleteListener<T> {
    return OnCompleteListener {
        when {
            it.isSuccessful -> cont.resume(Result.of(Unit))
            it.isCanceled -> cont.resume(Result.error(FirebaseCancelledException()))
            else -> cont.resume(Result.error(it.exception ?: Exception("A firebase error has occured!")))
        }
    }
}

inline fun <T, reified R : Any> generateAddResultCompletionListener(cont: Continuation<Result<R, Exception>>): OnCompleteListener<T> {
    return OnCompleteListener {
        val result = it.result as? R

        when {
            result == null -> {
                cont.resume(Result.error(EmptyResultException()))
            }
            it.isSuccessful -> cont.resume(Result.success(result))
            it.isCanceled -> cont.resume(Result.error(FirebaseCancelledException()))
            else -> cont.resume(Result.error(it.exception ?: Exception("A firebase error has occured!")))
        }
    }
}

class FirebaseCancelledException : Exception()
class EmptyResultException : Exception()