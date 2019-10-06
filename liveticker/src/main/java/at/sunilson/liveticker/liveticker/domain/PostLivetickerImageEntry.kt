package at.sunilson.liveticker.liveticker.domain

import android.net.Uri
import at.sunilson.liveticker.core.usecases.AsyncUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult

class PostLivetickerImageEntry(private val repository: LivetickerRepository) :
    AsyncUseCase<String, Exception, PostLivetickerImageEntry.Params>() {

    override suspend fun run(params: Params) =
        repository.addImageEntry(params.livetickerId, params.uri, params.caption)

    data class Params(val livetickerId: String, val caption: String, val uri: Uri)
}