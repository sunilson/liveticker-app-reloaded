package at.sunilson.liveticker.login.domain

import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.login.domain.models.LoginCredentials
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.SuspendableResult

class RegisterUseCase(private val authenticationRepository: IAuthenticationRepository) :
    AsyncUseCase<Unit, LoginCredentials>() {
    override suspend fun run(params: LoginCredentials): SuspendableResult<Unit, Exception> {

        //TODO validation

        return authenticationRepository.register(params.email, params.userName, params.password)
    }
}