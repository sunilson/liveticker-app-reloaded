package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.IllegalArgumentException

data class GetLivetickerParams(val livetickerId: String? = null, val livetickerSharingId: String? = null)

class GetLivetickerUseCase(private val livetickerRepository: LivetickerRepository) :
    AsyncFlowUseCase<LiveTicker, Exception, GetLivetickerParams>() {
    override fun run(params: GetLivetickerParams): Flow<SuspendableResult<LiveTicker, Exception>> {
        return when {
            params.livetickerId != null -> livetickerRepository.getLiveTickerUpdates(params.livetickerId)
            params.livetickerSharingId != null -> livetickerRepository.getLiveTickerUpdatesFromSharingid(params.livetickerSharingId)
            else -> flow { emit(SuspendableResult.error(IllegalArgumentException())) }
        }
    }
}