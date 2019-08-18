package at.sunilson.liveticker.login.domain

import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.usecases.AsyncUseCase
import at.sunilson.liveticker.core.isValidEmail
import at.sunilson.liveticker.login.domain.models.LoginCredentials
import com.github.kittinunf.result.coroutines.SuspendableResult

class RegisterUseCase(private val authenticationRepository: IAuthenticationRepository) :
    AsyncUseCase<Unit, Exception, LoginCredentials>() {
    override suspend fun run(params: LoginCredentials): SuspendableResult<Unit, Exception> {
        if (params.email.isNullOrEmpty() || !params.email.isValidEmail()) throw EmailInvalid()
        if (params.password.isNullOrEmpty()) throw PasswordInvalid()
        if (params.userName.isNullOrEmpty()) throw UsernameInvalid()
        return authenticationRepository.register(params.email, params.userName, params.password)
    }
}