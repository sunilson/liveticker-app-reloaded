package at.sunilson.liveticker.core

import com.github.kittinunf.result.coroutines.SuspendableResult

fun String.isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun <V: Any, E: Exception> SuspendableResult<V, E>.getOrNull(): V? {
    return try {
        get()
    } catch (error: Exception) {
        null
    }
}