package at.sunilson.liveticker.login.domain

import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.usecases.AsyncUseCase
import at.sunilson.liveticker.core.isValidEmail
import at.sunilson.liveticker.login.domain.models.LoginCredentials
import com.github.kittinunf.result.coroutines.SuspendableResult

class LoginUsecase(private val authenticationRepository: IAuthenticationRepository) :
    AsyncUseCase<Unit, Exception, LoginCredentials>() {
    override suspend fun run(params: LoginCredentials): SuspendableResult<Unit, Exception> {
        if(params.email.isNullOrEmpty() || !params.email.isValidEmail()) throw EmailInvalid()
        if(params.password.isNullOrEmpty()) throw PasswordInvalid()
        return authenticationRepository.login(params.email, params.password)
    }
}