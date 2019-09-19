package at.sunilson.liveticker.core

import android.app.Fragment
import android.content.Context
import android.content.Intent
import android.util.Log
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileReader

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