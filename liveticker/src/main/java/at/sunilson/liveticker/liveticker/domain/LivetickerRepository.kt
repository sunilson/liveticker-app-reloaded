package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow

interface LivetickerRepository {
    fun getLiveTickerUpdates(id: String): Flow<SuspendableResult<LiveTicker, Exception>>
    fun getLiveTickerUpdatesFromSharingid(id: String): Flow<SuspendableResult<LiveTicker, Exception>>
    fun getComments(id: String): Flow<SuspendableResult<List<Comment>, Exception>>
    suspend fun cheer(livetickerId: String)
    suspend fun addComment(livetickerId: String, comment: String, name: String): SuspendableResult<String, Exception>
}