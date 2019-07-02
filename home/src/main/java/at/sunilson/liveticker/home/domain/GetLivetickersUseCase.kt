package at.sunilson.liveticker.home.domain

import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.ObservationResult
import at.sunilson.liveticker.home.data.HomeRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.channels.ReceiveChannel


class GetLivetickersUseCase(private val homeRepository: HomeRepository) :
    AsyncUseCase<ReceiveChannel<SuspendableResult<ObservationResult<LiveTicker>, Exception>>, String>() {

    override suspend fun run(params: String): SuspendableResult<ReceiveChannel<SuspendableResult<ObservationResult<LiveTicker>, Exception>>, Exception> {
        return SuspendableResult.Success(homeRepository.getLivetickers(params))
    }
}