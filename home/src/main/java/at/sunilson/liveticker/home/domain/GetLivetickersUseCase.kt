package at.sunilson.liveticker.home.domain

import at.sunilson.liveticker.core.AsyncUseCase
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.ObservationResult
import at.sunilson.liveticker.home.data.HomeRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow


class GetLivetickersUseCase(private val homeRepository: HomeRepository) :
    AsyncUseCase<Flow<SuspendableResult<List<ObservationResult<LiveTicker>>, Exception>>, String>() {

    override suspend fun run(params: String): SuspendableResult<Flow<SuspendableResult<List<ObservationResult<LiveTicker>>, Exception>>, Exception> {
        return SuspendableResult.Success(homeRepository.getLivetickers(params))
    }
}