package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.liveticker.data.LivetickerRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.channels.ReceiveChannel

class GetLivetickerUseCase(private val livetickerRepository: LivetickerRepository) :
    AsyncUseCase<ReceiveChannel<SuspendableResult<LiveTicker, Exception>>, String>() {

    override suspend fun run(params: String): SuspendableResult<ReceiveChannel<SuspendableResult<LiveTicker, Exception>>, Exception> {
        return SuspendableResult.Success(livetickerRepository.getLiveTicker(params))
    }
}