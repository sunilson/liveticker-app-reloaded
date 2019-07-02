package at.sunilson.liveticker.home.domain

import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.home.data.HomeRepository
import com.github.kittinunf.result.coroutines.SuspendableResult

class DeleteLivetickerUsecase(private val homeRepository: HomeRepository) : AsyncUseCase<Unit, String>() {
    override suspend fun run(params: String): SuspendableResult<Unit, Exception> {
       return homeRepository.deleteLiveticker(params)
    }
}