package at.sunilson.liveticker.liveticker.data

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.*
import at.sunilson.liveticker.firebasecore.models.FirebaseComment
import at.sunilson.liveticker.firebasecore.models.FirebaseLiveticker
import at.sunilson.liveticker.firebasecore.models.convertToDomainEntity
import at.sunilson.liveticker.liveticker.domain.LivetickerRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.flatMap
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

    override fun getLiveTickerUpdates(id: String): Flow<SuspendableResult<LiveTicker, FirebaseOperationException>> {
        return fireStore
            .document("livetickers/$id")
            .observe<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }
    }

    override fun getLiveTickerUpdatesFromSharingid(id: String): Flow<SuspendableResult<LiveTicker, FirebaseOperationException>> {
        return fireStore
            .collection("livetickers")
            .whereEqualTo("sharingUrl", id)
            .observe<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }
            .map { result ->
                result.map { it.first() }
            }
    }

    override fun getComments(id: String): Flow<SuspendableResult<List<Comment>, FirebaseOperationException>> {
        return fireStore
            .collection("livetickers/$id/comments")
            .observe<FirebaseComment, Comment> { it.convertToDomainEntity() }
    }

    override suspend fun addComment(
        livetickerId: String,
        comment: String,
        name: String
    ): SuspendableResult<String, FirebaseOperationException> {
        return fireStore
            .collection("livetickers/$livetickerId/comments")
            .awaitAdd(FirebaseComment(name, comment))
    }

    override suspend fun setNotificationsDisabled(
        userId: String,
        livetickerId: String
    ): SuspendableResult<Unit, FirebaseOperationException> {
        val batch = fireStore.batch()

        val livetickerRef = fireStore
            .collection("notifications")
            .document(livetickerId)
        batch.update(livetickerRef, mapOf("subscribers" to FieldValue.arrayRemove(userId)))

        val userRef = fireStore
            .collection("users")
            .document(userId)
        batch.update(userRef, (mapOf("subscribedTo" to FieldValue.arrayRemove(livetickerId))))

        return batch.awaitCommit()
    }

    override suspend fun setNotificationsEnabled(
        userId: String,
        livetickerId: String
    ): SuspendableResult<Unit, FirebaseOperationException> {
        val batch = fireStore.batch()

        val livetickerRef = fireStore
            .collection("notifications")
            .document(livetickerId)
        batch.update(livetickerRef, mapOf("subscribers" to FieldValue.arrayUnion(userId)))

        val userRef = fireStore
            .collection("users")
            .document(userId)
        batch.update(userRef, (mapOf("subscribedTo" to FieldValue.arrayUnion(livetickerId))))

        return batch.awaitCommit()
    }
}