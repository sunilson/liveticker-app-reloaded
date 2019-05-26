package at.sunilson.liveticker.firebasecore

import com.google.android.gms.tasks.OnCompleteListener
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume

fun <T> generateCompleteListener(cont: Continuation<ActionResult>): OnCompleteListener<T> {
    return OnCompleteListener {
        when {
            it.isSuccessful -> cont.resume(ActionResult.SUCCESS)
            it.isCanceled -> cont.resume(ActionResult.CANCELLED)
            else -> cont.resume(ActionResult.FAILED)
        }
    }
}