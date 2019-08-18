package at.sunilson.liveticker.core.usecases

import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Base AsyncUseCase that launches an abstract function in a Coroutine and returns the result to a callback
 */
abstract class AsyncUseCase<out Result : Any, out Exception: kotlin.Exception, in Params> {
    abstract suspend fun run(params: Params): SuspendableResult<Result, Exception>
    suspend operator fun invoke(params: Params): SuspendableResult<Result, Exception> {
        return withContext(Dispatchers.IO) { run(params) }
    }
}