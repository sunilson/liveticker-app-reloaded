package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.liveticker.data.LivetickerRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow

class GetLivetickerUseCase(private val livetickerRepository: LivetickerRepository) :
    AsyncUseCase<Flow<SuspendableResult<LiveTicker, Exception>>, String>() {

    override suspend fun run(params: String): SuspendableResult<Flow<SuspendableResult<LiveTicker, Exception>>, Exception> {
        return SuspendableResult.Success(livetickerRepository.getLiveTickerUpdates(params))
    }
}