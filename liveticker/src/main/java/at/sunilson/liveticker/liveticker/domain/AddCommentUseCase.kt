package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.authentication.IAuthenticationRepository
import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.liveticker.data.LivetickerRepository
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.SuspendableResult

data class AddCommentParams(val id: String, val text: String)

class AddCommentUseCase(
    private val livetickerRepository: LivetickerRepository,
    private val authenticationRepository: IAuthenticationRepository
) :
    AsyncUseCase<String, AddCommentParams>() {
    override suspend fun run(params: AddCommentParams): SuspendableResult<String, Exception> {

        val (user, authError) = authenticationRepository.getCurrentUserNow()

        return livetickerRepository.addComment(
            params.id,
            params.text,
            user?.name ?: "Anonymous"
        )
    }
}