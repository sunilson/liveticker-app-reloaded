package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.authentication.AuthenticationException
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.usecases.AsyncUseCase
import at.sunilson.liveticker.firebasecore.FirebaseOperationException
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.mapError

data class SetNotificationsParams(val enable: Boolean, val livetickerId: String)

sealed class SetNotificationException : Exception() {
    class AuthenticationFailed(val authenticationException: AuthenticationException?) : SetNotificationException()
    class DatabaseException(val firebaseOperationException: FirebaseOperationException) : SetNotificationException()
}

class SetNotificationUseCase(
    private val repository: LivetickerRepository,
    private val authenticationRepository: IAuthenticationRepository
) : AsyncUseCase<Unit, SetNotificationException, SetNotificationsParams>() {
    override suspend fun run(params: SetNotificationsParams): SuspendableResult<Unit, SetNotificationException> {

        val (user, authError) = authenticationRepository.getCurrentUserNow()

        if (authError != null || user == null) {
            return SuspendableResult.error(SetNotificationException.AuthenticationFailed(authError))
        }

        return if (params.enable) {
            repository.setNotificationsEnabled(user.id, params.livetickerId)
                .mapError { SetNotificationException.DatabaseException(it) }
        } else {
            repository.setNotificationsDisabled(user.id, params.livetickerId)
                .mapError { SetNotificationException.DatabaseException(it) }
        }
    }
}