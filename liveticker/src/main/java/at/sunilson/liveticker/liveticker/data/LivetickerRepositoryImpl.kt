package at.sunilson.liveticker.liveticker.data

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.MediaStore
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.*
import at.sunilson.liveticker.firebasecore.models.*
import at.sunilson.liveticker.liveticker.domain.LivetickerRepository
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.flatMap
import com.github.kittinunf.result.coroutines.map
import com.github.kittinunf.result.coroutines.mapError
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

internal class LiveTickerRepositoryImpl(
    private val fireStore: FirebaseFirestore,
    private val firebaseStorage: FirebaseStorage,
    private val contentResolver: ContentResolver
) : LivetickerRepository {

    override suspend fun cheer(livetickerId: String) {
        fireStore
            .document("livetickers/$livetickerId")
            .awaitUpdate(mapOf("cheers" to FieldValue.increment(1)))
    }

    override fun getLivetickerEntries(id: String) =
        fireStore
            .collection("livetickers/$id/entries")
            .observe<FirebaseLivetickerEntry, LiveTickerEntry> { it.convertToDomainEntity() }

    override fun getLiveTickerUpdates(id: String) = fireStore
        .document("livetickers/$id")
        .observe<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }

    override fun getLiveTickerUpdatesFromSharingid(id: String) = fireStore
        .collection("livetickers")
        .whereEqualTo("sharingUrl", id)
        .observe<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }
        .map { result ->
            result.map { it.first() }
        }

    override fun getComments(id: String) = fireStore
        .collection("livetickers/$id/comments")
        .observe<FirebaseComment, Comment> { it.convertToDomainEntity() }

    override suspend fun addComment(
        livetickerId: String,
        comment: String,
        name: String
    ) = fireStore
        .collection("livetickers/$livetickerId/comments")
        .awaitAdd(FirebaseComment(name, comment))

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

    override suspend fun getLocalImagePaths() = SuspendableResult.of<List<Uri>, Exception> {
        val result = mutableListOf<Uri>()
        val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection =
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATE_ADDED)
        contentResolver.query(uri, projection, null, null, "DATE_ADDED DESC")?.use {
            while (it.moveToNext()) {
                result.add(
                    ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        it.getLong(0)
                    )
                )
            }
        }
        result
    }

    override suspend fun addTextEntry(
        livetickerId: String,
        text: String
    ) = fireStore
        .collection("livetickers/$livetickerId/entries")
        .awaitAdd(
            LiveTickerEntry.TextLiveTickerEntry(
                "",
                0L,
                "",
                text
            ).convertToFirebaseEntity()
        )

    override suspend fun addImageEntry(
        id: String,
        uri: Uri,
        caption: String
    ) = firebaseStorage
        .reference
        .child("$id/${System.currentTimeMillis()}.jpg")
        .awaitPut(uri)
        .flatMap {
            fireStore
                .collection("livetickers/$id/entries")
                .awaitAdd(
                    LiveTickerEntry.ImageLivetickerEntry(
                        "",
                        0L,
                        caption,
                        it
                    ).convertToFirebaseEntity()
                )
        }.mapError {
            it
        }.map {
            it
        }
}