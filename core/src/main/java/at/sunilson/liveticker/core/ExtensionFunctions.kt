package at.sunilson.liveticker.core

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader
import java.lang.String.format
import java.util.*

fun String.isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun <V : Any, E : Exception> SuspendableResult<V, E>.getOrNull(): V? {
    return try {
        get()
    } catch (error: Exception) {
        null
    }
}

suspend fun <T> Iterable<T>.forEachParallelCatching(block: suspend (T) -> Unit) = coroutineScope {
    map { async { block(it) } }.forEach {
        try {
            it.await()
        } catch (e: Throwable) {
            Log.e("", "")
        }
    }
}

suspend fun <T> Iterable<T>.forEachParallel(block: suspend (T) -> Unit) = coroutineScope {
    map { async { block(it) } }.forEach { it.await() }
}

suspend fun <T, R> Iterable<T>.mapParallel(block: suspend (T) -> R): Iterable<R> = coroutineScope {
    map { async { block(it) } }.map { it.await() }
}

suspend fun doParallel(vararg blocks: suspend () -> Unit) = coroutineScope {
    blocks
        .map { async { it() } }
        .forEach { it.await() }
}

suspend fun <T> doParallelWithResult(vararg blocks: suspend () -> T) =
    withContext(Dispatchers.Default) {
        blocks.map { async { it() } }.map { it.await() }
    }

fun File.readable() = try {
    val fileReader = FileReader(absolutePath)
    fileReader.read()
    fileReader.close()
    true
} catch (e: Exception) {
    false
}

inline fun <T : Any, E : Exception, R : Any> Flow<SuspendableResult<List<T>, E>>.mapContents(
    crossinline transform: (T) -> R
) =
    map { it.map { it.map(transform) } }

inline fun <T : Any, E : Exception> Flow<SuspendableResult<List<T>, E>>.filterContents(crossinline predicate: (T) -> Boolean) =
    map { it.map { it.filter(predicate) } }

inline fun <T : Any, E : Exception, R : Comparable<R>> Flow<SuspendableResult<List<T>, E>>.sortContents(
    crossinline selector: (T) -> R?
) = map { it.map { it.sortedBy(selector) } }

inline fun <T : Any, E : Exception, R : Comparable<R>> Flow<SuspendableResult<List<T>, E>>.sortContentsDescending(
    crossinline selector: (T) -> R?
) = map { it.map { it.sortedByDescending(selector) } }

fun Int.padZero() = format("%02d", Integer.parseInt(this.toString()))