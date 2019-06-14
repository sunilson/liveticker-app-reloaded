package at.sunilson.liveticker.network

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import com.github.kittinunf.result.Result
import kotlinx.coroutines.channels.ReceiveChannel

interface IRemoteRepository {
    fun getComments(id: String): ReceiveChannel<List<Comment>>
    fun getLiveTickerEntries(id: String): ReceiveChannel<List<LiveTickerEntry>>

    suspend fun updateLiveticker(liveTicker: LiveTicker): Result<LiveTicker, Exception>
    suspend fun addComment(id: String, comment: Comment): Result<Comment, Exception>
    suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry): Result<LiveTickerEntry, Exception>
    suspend fun getSharingLink(id: String): String
    suspend fun getEditLink(id: String): String
}