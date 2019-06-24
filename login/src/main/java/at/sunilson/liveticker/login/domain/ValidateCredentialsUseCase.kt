package at.sunilson.liveticker.login.domain

import at.sunilson.liveticker.core.UseCase
import at.sunilson.liveticker.login.domain.models.LoginCredentials
import com.github.kittinunf.result.Result

class ValidateCredentialsUseCase : UseCase<LoginCredentials, Unit>() {
    override fun run(params: Unit): Result<LoginCredentials, Exception> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}