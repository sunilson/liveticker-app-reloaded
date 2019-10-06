package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.usecases.AsyncUseCase


class PostLivetickerTextEntry(private val repository: LivetickerRepository) :
    AsyncUseCase<String, Exception, PostLivetickerTextEntry.Params>() {
    override suspend fun run(params: Params) =
        repository.addTextEntry(params.livetickerId, params.text)

    data class Params(val text: String, val livetickerId: String)
}