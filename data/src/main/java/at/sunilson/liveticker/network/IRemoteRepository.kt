package at.sunilson.liveticker.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.ActionResult
import at.sunilson.liveticker.network.util.CollectionLiveData
import at.sunilson.liveticker.network.util.DocumentLiveData
import kotlinx.coroutines.channels.ReceiveChannel

interface IRemoteRepository {
    fun getComments(id: String): ReceiveChannel<List<Comment>>
    fun getLivetickers(userId: String): ReceiveChannel<List<LiveTicker>>
    fun getLiveTicker(id: String): ReceiveChannel<LiveTicker>
    fun getLiveTickerEntries(id: String): ReceiveChannel<List<LiveTickerEntry>>

    suspend fun createLiveticker(liveTicker: LiveTicker): ActionResult
    suspend fun updateLiveticker(liveTicker: LiveTicker): ActionResult
    suspend fun addComment(id: String, comment: Comment): ActionResult
    suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry): ActionResult
    suspend fun getSharingLink(id: String): String
    suspend fun getEditLink(id: String): String
}