package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.usecases.AsyncUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult

class CheerUseCase(private val livetickerRepository: LivetickerRepository) : AsyncUseCase<Unit, Exception, String>() {
    override suspend fun run(params: String): SuspendableResult<Unit, Exception> {
        return SuspendableResult.of {
            livetickerRepository.cheer(params)
        }
    }
}