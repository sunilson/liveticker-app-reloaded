package at.sunilson.liveticker.liveticker.domain

import android.net.Uri
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.FirebaseOperationException
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.SuspendableResult
import kotlinx.coroutines.flow.Flow

interface LivetickerRepository {
    fun getLiveTickerUpdates(id: String): Flow<SuspendableResult<LiveTicker, FirebaseOperationException>>
    fun getLiveTickerUpdatesFromSharingid(id: String): Flow<SuspendableResult<LiveTicker, FirebaseOperationException>>
    fun getComments(id: String): Flow<SuspendableResult<List<Comment>, FirebaseOperationException>>
    suspend fun getLocalImagePaths(): SuspendableResult<List<Uri>, Exception>

    suspend fun cheer(livetickerId: String)
    suspend fun addComment(
        livetickerId: String,
        comment: String,
        name: String
    ): SuspendableResult<String, FirebaseOperationException>

    suspend fun setNotificationsEnabled(
        userId: String,
        livetickerId: String
    ): SuspendableResult<Unit, FirebaseOperationException>

    suspend fun setNotificationsDisabled(
        userId: String,
        livetickerId: String
    ): SuspendableResult<Unit, FirebaseOperationException>
}