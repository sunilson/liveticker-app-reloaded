package at.sunilson.liveticker.core.usecases

import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

/**
 * Base AsyncUseCase that returns a Flow which can emit [SuspendableResult] multiple times (cached, network, etc.)
 */
abstract class AsyncFlowUseCase<out Result : Any, out Exception : kotlin.Exception, in Params> {
    abstract fun run(params: Params): Flow<SuspendableResult<Result, Exception>>
    operator fun invoke(params: Params): Flow<SuspendableResult<Result, Exception>> {
        return run(params).flowOn(Dispatchers.IO)
    }
}