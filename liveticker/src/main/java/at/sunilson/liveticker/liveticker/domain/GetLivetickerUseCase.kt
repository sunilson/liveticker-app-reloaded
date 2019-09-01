package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import at.sunilson.liveticker.firebasecore.FirebaseOperationException
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.mapError
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

data class GetLivetickerParams(
    val livetickerId: String? = null,
    val livetickerSharingId: String? = null
)

sealed class GetLivetickerException : Exception() {
    class InvalidParamsException : GetLivetickerException()
    class DatabaseException(val firebaseOperationException: FirebaseOperationException) :
        GetLivetickerException()
}

class GetLivetickerUseCase(private val livetickerRepository: LivetickerRepository) :
    AsyncFlowUseCase<LiveTicker, GetLivetickerException, GetLivetickerParams>() {
    override fun run(params: GetLivetickerParams): Flow<SuspendableResult<LiveTicker, GetLivetickerException>> {
        return when {
            params.livetickerId != null -> livetickerRepository.getLiveTickerUpdates(params.livetickerId).map {
                it.mapError { GetLivetickerException.DatabaseException(it) }
            }
            params.livetickerSharingId != null -> livetickerRepository.getLiveTickerUpdatesFromSharingid(
                params.livetickerSharingId
            ).map {
                it.mapError { GetLivetickerException.DatabaseException(it) }
            }
            else -> flow { emit(SuspendableResult.error(GetLivetickerException.InvalidParamsException())) }
        }
    }
}