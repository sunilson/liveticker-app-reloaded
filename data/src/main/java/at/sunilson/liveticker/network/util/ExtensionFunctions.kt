package at.sunilson.liveticker.network.util

import at.sunilson.liveticker.firebasecore.ActionResult
import at.sunilson.liveticker.firebasecore.generateCompleteListener
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
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