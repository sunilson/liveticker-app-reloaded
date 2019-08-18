package at.sunilson.liveticker.home.domain

import at.sunilson.liveticker.core.ObservationResult
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.sharing.domain.models.NoEditUrlFoundException
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getLivetickers(userId: String): Flow<SuspendableResult<List<LiveTicker>, Exception>>
    suspend fun deleteLiveticker(id: String): SuspendableResult<Unit, Exception>
}