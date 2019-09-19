package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.authentication.AuthenticationException
import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import at.sunilson.liveticker.firebasecore.FirebaseOperationException
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.map
import com.github.kittinunf.result.coroutines.mapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

data class GetLivetickerParams(
    val livetickerId: String? = null,
    val livetickerSharingId: String? = null
)

data class GetLivetickerResult(val liveticker: LiveTicker, val isAuthor: Boolean)

sealed class GetLivetickerException : Exception() {
    class InvalidParamsException : GetLivetickerException()

    class DatabaseException(val firebaseOperationException: FirebaseOperationException) :
        GetLivetickerException()

    class AuthenticationFailed(val authenticationException: AuthenticationException) :
        GetLivetickerException()
}

class GetLivetickerUseCase(
    private val livetickerRepository: LivetickerRepository,
    private val authenticationRepository: IAuthenticationRepository
) : AsyncFlowUseCase<GetLivetickerResult, GetLivetickerException, GetLivetickerParams>() {
    override fun run(params: GetLivetickerParams): Flow<SuspendableResult<GetLivetickerResult, GetLivetickerException>> {
        val (user, error) = authenticationRepository.getCurrentUserNow()

        //If no user is logged in, return error
        if (error != null || user == null) {
            return flowOf(
                SuspendableResult.error(
                    GetLivetickerException.AuthenticationFailed(
                        error ?: AuthenticationException.NotLoggedIn()
                    )
                )
            )
        }

        return when {
            params.livetickerId != null -> livetickerRepository.getLiveTickerUpdates(params.livetickerId).map {
                it
                    .map { GetLivetickerResult(it, user.id == it.authorId) }
                    .mapError { GetLivetickerException.DatabaseException(it) }
            }
            params.livetickerSharingId != null -> {
                livetickerRepository.getLiveTickerUpdatesFromSharingid(params.livetickerSharingId)
                    .map {
                        it
                            .map { GetLivetickerResult(it, user.id == it.authorId) }
                            .mapError { GetLivetickerException.DatabaseException(it) }
                    }
            }
            else -> flow { emit(SuspendableResult.error(GetLivetickerException.InvalidParamsException())) }
        }
    }
}