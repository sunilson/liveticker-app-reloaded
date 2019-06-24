package at.sunilson.liveticker.network

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.*
import com.github.kittinunf.result.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ReceiveChannel

internal class FirebaseRepository(private val fireStore: FirebaseFirestore) : IRemoteRepository {

    override fun getComments(id: String): ReceiveChannel<ObservationResult<Comment>> {
        return fireStore
            .collection("livetickers/$id/comments")
            .observeChanges()
    }

    override fun getLiveTickerEntries(id: String): ReceiveChannel<ObservationResult<LiveTickerEntry>> {
        return fireStore
            .collection("livetickers/$id/entries")
            .observeChanges()
    }

    override suspend fun updateLiveticker(liveTicker: LiveTicker): Result<Unit, Exception> {
        return fireStore.document("livetickers/${liveTicker.id}").awaitSet(liveTicker)
    }

    override suspend fun addComment(id: String, comment: Comment): Result<String, Exception> {
        return fireStore.collection("livetickers/$id/comments").awaitAdd(comment)
    }

    override suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry): Result<String, Exception> {
        return fireStore.collection("livetickers/$id/entries").awaitAdd(liveTickerEntry)
    }

    override suspend fun getSharingLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getEditLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}