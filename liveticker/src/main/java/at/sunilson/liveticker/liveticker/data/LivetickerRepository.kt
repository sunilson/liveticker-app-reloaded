package at.sunilson.liveticker.liveticker.data

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.awaitAdd
import at.sunilson.liveticker.firebasecore.awaitUpdate
import at.sunilson.liveticker.firebasecore.models.FirebaseComment
import at.sunilson.liveticker.firebasecore.models.FirebaseLiveticker
import at.sunilson.liveticker.firebasecore.models.convertToDomainEntity
import at.sunilson.liveticker.firebasecore.observe
import at.sunilson.liveticker.liveticker.domain.LivetickerRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.map
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class LiveTickerRepositoryImpl(private val fireStore: FirebaseFirestore) : LivetickerRepository {

    override suspend fun cheer(livetickerId: String) {
        fireStore
            .document("livetickers/$livetickerId")
            .awaitUpdate(mapOf("cheers" to FieldValue.increment(1)))
    }

    override fun getLiveTickerUpdates(id: String): Flow<SuspendableResult<LiveTicker, Exception>> {
        return fireStore
            .document("livetickers/$id")
            .observe<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }
    }

    override fun getLiveTickerUpdatesFromSharingid(id: String): Flow<SuspendableResult<LiveTicker, Exception>> {
        return fireStore
            .collection("livetickers")
            .whereEqualTo("sharingUrl", id)
            .observe<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }
            .map { result ->
                result.map { it.first() }
            }
    }

    override fun getComments(id: String): Flow<SuspendableResult<List<Comment>, Exception>> {
        return fireStore
            .collection("livetickers/$id/comments")
            .observe<FirebaseComment, Comment> { it.convertToDomainEntity() }
    }

    override suspend fun addComment(
        livetickerId: String,
        comment: String,
        name: String
    ): SuspendableResult<String, Exception> {
        return fireStore
            .collection("livetickers/$livetickerId/comments")
            .awaitAdd(FirebaseComment(name, comment))
    }
}