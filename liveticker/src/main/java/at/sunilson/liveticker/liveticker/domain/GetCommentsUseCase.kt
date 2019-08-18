package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow

class GetCommentsUseCase(private val livetickerRepository: LivetickerRepository) :
    AsyncFlowUseCase<List<Comment>, Exception, String>() {

    override fun run(params: String): Flow<SuspendableResult<List<Comment>, Exception>> {
        return livetickerRepository.getComments(params)
    }
}