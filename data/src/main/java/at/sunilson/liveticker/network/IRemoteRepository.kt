package at.sunilson.liveticker.network

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.ObservationResult
import com.github.kittinunf.result.Result
import kotlinx.coroutines.channels.ReceiveChannel

interface IRemoteRepository {
    fun getComments(id: String): ReceiveChannel<ObservationResult<Comment>>
    fun getLiveTickerEntries(id: String): ReceiveChannel<ObservationResult<LiveTickerEntry>>

    suspend fun updateLiveticker(liveTicker: LiveTicker): Result<Unit, Exception>
    suspend fun addComment(id: String, comment: Comment): Result<String, Exception>
    suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry): Result<String, Exception>
    suspend fun getSharingLink(id: String): String
    suspend fun getEditLink(id: String): String
}