package at.sunilson.liveticker.sharing.domain

import at.sunilson.liveticker.core.usecases.AsyncUseCase
import at.sunilson.liveticker.sharing.domain.models.NoEditUrlFoundException

class GetEditUrlUseCase(private val repository: SharingRepository) :
    AsyncUseCase<String, NoEditUrlFoundException, String>() {
    override suspend fun run(params: String) = repository.getEditUrl(params)
}