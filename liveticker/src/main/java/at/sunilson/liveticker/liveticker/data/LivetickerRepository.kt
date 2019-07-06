package at.sunilson.liveticker.liveticker.data

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.ObservationResult
import at.sunilson.liveticker.firebasecore.awaitAdd
import at.sunilson.liveticker.firebasecore.observe
import at.sunilson.liveticker.firebasecore.observeChanges
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ReceiveChannel

interface LivetickerRepository {
    fun getLiveTicker(id: String): ReceiveChannel<SuspendableResult<LiveTicker, Exception>>
    fun getComments(id: String): ReceiveChannel<SuspendableResult<ObservationResult<Comment>, Exception>>
    suspend fun addComment(livetickerId: String, comment: String, name: String): SuspendableResult<String, Exception>
}

internal class LiveTickerRepositoryImpl(private val fireStore: FirebaseFirestore) : LivetickerRepository {

    override fun getLiveTicker(id: String): ReceiveChannel<SuspendableResult<LiveTicker, Exception>> {
        return fireStore
            .document("livetickers/$id")
            .observe()
    }

    override fun getComments(id: String): ReceiveChannel<SuspendableResult<ObservationResult<Comment>, Exception>> {
        return fireStore
            .collection("livetickers/$id/comments")
            .observeChanges()
    }

    override suspend fun addComment(
        livetickerId: String,
        comment: String,
        name: String
    ): SuspendableResult<String, Exception> {
        return fireStore.collection("livetickers/$livetickerId/comments").awaitAdd(Comment(name, comment))
    }
}