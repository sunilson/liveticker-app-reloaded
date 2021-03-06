package at.sunilson.liveticker.network

import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.network.util.CollectionLiveData
import at.sunilson.liveticker.network.util.DocumentLiveData

interface IRemoteRepository {
    fun getComments(id: String): CollectionLiveData<Comment>
    fun getLivetickers(userId: String): CollectionLiveData<LiveTicker>
    fun getLiveTicker(id: String): DocumentLiveData<LiveTicker>
    fun getLiveTickerEntries(id: String): CollectionLiveData<LiveTickerEntry>

    suspend fun createLiveticker(liveTicker: LiveTicker)
    suspend fun updateLiveticker(liveTicker: LiveTicker)
    suspend fun addComment(id: String, comment: Comment)
    suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry)
    suspend fun getSharingLink(id: String): String
    suspend fun getEditLink(id: String): String
}