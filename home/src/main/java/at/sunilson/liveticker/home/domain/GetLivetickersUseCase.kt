package at.sunilson.liveticker.home.domain

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.ObservationResult
import at.sunilson.liveticker.core.forEachParallel
import at.sunilson.liveticker.core.forEachParallelCatching
import at.sunilson.liveticker.core.usecases.AsyncFlowUseCase
import at.sunilson.liveticker.sharing.domain.GetEditUrlUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.success
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach


class GetLivetickersUseCase(
    private val homeRepository: HomeRepository,
    private val getEditUrlUseCase: GetEditUrlUseCase
) : AsyncFlowUseCase<List<LiveTicker>, Exception, String>() {

    override fun run(params: String): Flow<SuspendableResult<List<LiveTicker>, Exception>> {
        return homeRepository.getLivetickers(params).onEach {
            //Prefetch edit urls
            //TODO Only if chance on succes eg. author
            it.success { livetickerList ->
                livetickerList.forEachParallelCatching { liveticker -> getEditUrlUseCase(liveticker.id) }
            }
        }
    }
}