package at.sunilson.liveticker.network.util

import at.sunilson.liveticker.network.models.FirebaseTaskResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

fun <T> generateCompleteListener(cont: Continuation<FirebaseTaskResult>): OnCompleteListener<T> {
    return OnCompleteListener {
        when {
            it.isSuccessful -> cont.resume(FirebaseTaskResult.SUCCESS)
            it.isCanceled -> cont.resume(FirebaseTaskResult.CANCELLED)
            else -> cont.resume(FirebaseTaskResult.FAILED)
        }
    }
}

suspend fun DocumentReference.awaitDelete() {
    suspendCancellableCoroutine<FirebaseTaskResult> { cont ->
        delete().addOnCompleteListener(generateCompleteListener(cont))
    }
}

suspend fun DocumentReference.awaitSet(data: Any) {
    suspendCancellableCoroutine<FirebaseTaskResult> { cont ->
        set(data).addOnCompleteListener(generateCompleteListener(cont))
    }
}

suspend fun CollectionReference.awaitAdd(data: Any) {
    suspendCancellableCoroutine<FirebaseTaskResult> { cont ->
        add(data).addOnCompleteListener(generateCompleteListener(cont))
    }
}