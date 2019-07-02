package at.sunilson.liveticker.core

import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base AsyncUseCase that launches an abstract function in a Coroutine and returns the result to a callback
 */
abstract class AsyncUseCase<out R : Any, in Params> {
    abstract suspend fun run(params: Params): SuspendableResult<R, Exception>
    suspend operator fun invoke(params: Params): SuspendableResult<R, Exception> {
        return withContext(Dispatchers.IO) { run(params) }
    }
}