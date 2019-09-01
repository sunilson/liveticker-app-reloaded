package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.usecases.AsyncUseCase
import at.sunilson.liveticker.firebasecore.FirebaseOperationException
import com.github.kittinunf.result.coroutines.SuspendableResult

class CheerUseCase(private val livetickerRepository: LivetickerRepository) : AsyncUseCase<Unit, FirebaseOperationException, String>() {
    override suspend fun run(params: String): SuspendableResult<Unit, FirebaseOperationException> {
        return SuspendableResult.of {
            livetickerRepository.cheer(params)
        }
    }
}