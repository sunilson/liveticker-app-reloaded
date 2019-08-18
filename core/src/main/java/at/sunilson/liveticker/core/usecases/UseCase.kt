package at.sunilson.liveticker.core.usecases

import com.github.kittinunf.result.Result

abstract class UseCase<out R : Any, in Params> {
    abstract fun run(params: Params): Result<R, Exception>
    operator fun invoke(params: Params): Result<R, Exception> {
        return run(params)
    }
}