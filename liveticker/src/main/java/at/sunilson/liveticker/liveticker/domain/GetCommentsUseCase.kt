package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.firebasecore.ObservationResult
import at.sunilson.liveticker.liveticker.data.LivetickerRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.channels.ReceiveChannel

class GetCommentsUseCase(private val livetickerRepository: LivetickerRepository) :
    AsyncUseCase<ReceiveChannel<SuspendableResult<ObservationResult<Comment>, Exception>>, String>() {

    override suspend fun run(params: String): SuspendableResult<ReceiveChannel<SuspendableResult<ObservationResult<Comment>, Exception>>, Exception> {
        return SuspendableResult.Success(livetickerRepository.getComments(params))
    }
}