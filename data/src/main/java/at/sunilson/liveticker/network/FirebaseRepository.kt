package at.sunilson.liveticker.network

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.awaitAddResult
import at.sunilson.liveticker.firebasecore.awaitSetResult
import at.sunilson.liveticker.firebasecore.observe
import com.github.kittinunf.result.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ReceiveChannel

internal class FirebaseRepository(private val fireStore: FirebaseFirestore) : IRemoteRepository {

    override fun getComments(id: String): ReceiveChannel<List<Comment>> {
        return fireStore
            .collection("livetickers/$id/comments")
            .observe()
    }

    override fun getLiveTickerEntries(id: String): ReceiveChannel<List<LiveTickerEntry>> {
        return fireStore
            .collection("livetickers/$id/entries")
            .observe()
    }

    override suspend fun updateLiveticker(liveTicker: LiveTicker): Result<LiveTicker, Exception> {
        return fireStore.document("livetickers/${liveTicker.id}").awaitSetResult(liveTicker)
    }

    override suspend fun addComment(id: String, comment: Comment): Result<Comment, Exception> {
        return fireStore.collection("livetickers/$id/comments").awaitAddResult(comment)
    }

    override suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry): Result<LiveTickerEntry, Exception> {
        return fireStore.collection("livetickers/$id/entries").awaitAddResult(liveTickerEntry)
    }

    override suspend fun getSharingLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getEditLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}