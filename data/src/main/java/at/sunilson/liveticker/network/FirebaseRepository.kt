package at.sunilson.liveticker.network

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.ActionResult
import at.sunilson.liveticker.network.util.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ReceiveChannel

internal class FirebaseRepository(private val fireStore: FirebaseFirestore) : IRemoteRepository {

    override fun getComments(id: String): ReceiveChannel<List<Comment>> {
        return fireStore
            .collection("livetickers/$id/comments")
            .observe(Comment::class.java)
    }

    override fun getLivetickers(userId: String): ReceiveChannel<List<LiveTicker>> {
        return fireStore
            .collection("livetickers")
            .whereEqualTo("authorId", userId)
            .observe(LiveTicker::class.java)
    }

    override fun getLiveTicker(id: String): ReceiveChannel<LiveTicker> {
        return fireStore
            .document("livetickers/$id")
            .observe(LiveTicker::class.java)
    }

    override fun getLiveTickerEntries(id: String): ReceiveChannel<List<LiveTickerEntry>> {
        return fireStore
            .collection("livetickers/$id/entries")
            .observe(LiveTickerEntry::class.java)
    }


    override suspend fun createLiveticker(liveTicker: LiveTicker): ActionResult {
        return fireStore.collection("livetickers").awaitAdd(liveTicker)
    }

    override suspend fun updateLiveticker(liveTicker: LiveTicker): ActionResult {
        return fireStore.document("livetickers/${liveTicker.id}").awaitSet(liveTicker)
    }

    override suspend fun addComment(id: String, comment: Comment): ActionResult {
        return fireStore.collection("livetickers/$id/comments").awaitAdd(comment)
    }

    override suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry): ActionResult {
        return fireStore.collection("livetickers/$id/entries").awaitAdd(liveTickerEntry)
    }

    override suspend fun getSharingLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getEditLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}