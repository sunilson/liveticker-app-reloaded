package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.liveticker.data.LivetickerRepository
import com.github.kittinunf.result.coroutines.SuspendableResult

class CheerUseCase(private val livetickerRepository: LivetickerRepository) : AsyncUseCase<Unit, String>() {
    override suspend fun run(params: String): SuspendableResult<Unit, Exception> {
        return SuspendableResult.of {
            livetickerRepository.cheer(params)
        }
    }
}