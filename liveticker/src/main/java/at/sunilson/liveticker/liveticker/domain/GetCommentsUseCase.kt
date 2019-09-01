package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import at.sunilson.liveticker.firebasecore.FirebaseOperationException
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow

class GetCommentsUseCase(private val livetickerRepository: LivetickerRepository) :
    AsyncFlowUseCase<List<Comment>, FirebaseOperationException, String>() {

    override fun run(params: String): Flow<SuspendableResult<List<Comment>, FirebaseOperationException>> {
        return livetickerRepository.getComments(params)
    }
}