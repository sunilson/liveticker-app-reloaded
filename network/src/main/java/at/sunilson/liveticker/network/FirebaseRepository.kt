package at.sunilson.liveticker.network

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.network.util.CollectionLiveData
import at.sunilson.liveticker.network.util.DocumentLiveData
import at.sunilson.liveticker.network.util.awaitAdd
import at.sunilson.liveticker.network.util.awaitSet
import com.google.firebase.firestore.FirebaseFirestore

class FirebaseRepository(private val fireStore: FirebaseFirestore) : IRemoteRepository {

    override fun getComments(id: String): CollectionLiveData<Comment> {
        return CollectionLiveData(
            Comment::class.java,
            reference = fireStore.collection("livetickers/$id/comments")
        )
    }

    override fun getLivetickers(userId: String): CollectionLiveData<LiveTicker> {
        return CollectionLiveData(
            LiveTicker::class.java,
            query = fireStore.collection("livetickers").whereEqualTo("authorId", userId)
        )
    }

    override fun getLiveTicker(id: String): DocumentLiveData<LiveTicker> {
        return DocumentLiveData(fireStore.document("livetickers/$id"), LiveTicker::class.java)
    }

    override fun getLiveTickerEntries(id: String): CollectionLiveData<LiveTickerEntry> {
        return CollectionLiveData(
            LiveTickerEntry::class.java,
            reference = fireStore.collection("livetickers/$id/entries")
        )
    }


    override suspend fun createLiveticker(liveTicker: LiveTicker) {
        fireStore.collection("livetickers").awaitAdd(liveTicker)
    }

    override suspend fun updateLiveticker(liveTicker: LiveTicker) {
        fireStore.document("livetickers/${liveTicker.id}").awaitSet(liveTicker)
    }

    override suspend fun addComment(id: String, comment: Comment) {
        fireStore.collection("livetickers/$id/comments").awaitAdd(comment)
    }

    override suspend fun addLivetickerEntry(id: String, liveTickerEntry: LiveTickerEntry) {
        fireStore.collection("livetickers/$id/entries").awaitAdd(liveTickerEntry)
    }

    override suspend fun getSharingLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override suspend fun getEditLink(id: String): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}