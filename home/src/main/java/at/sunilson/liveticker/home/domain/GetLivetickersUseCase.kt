package at.sunilson.liveticker.home.domain

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.ObservationResult
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow


class GetLivetickersUseCase(private val homeRepository: HomeRepository) :
    AsyncFlowUseCase<List<LiveTicker>, Exception, String>() {

    override fun run(params: String): Flow<SuspendableResult<List<LiveTicker>, Exception>> {
        return homeRepository.getLivetickers(params)
    }
}