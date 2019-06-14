package at.sunilson.liveticker.livetickercreation.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.awaitAddResult
import com.github.kittinunf.result.Result
import com.google.firebase.firestore.FirebaseFirestore

interface LivetickerCreationRepository {
    suspend fun createLiveticker(liveTicker: LiveTicker): Result<LiveTicker, Exception>
}

internal class LivetickerCreationRepositoryImpl(private val fireStore: FirebaseFirestore) : LivetickerCreationRepository {
    override suspend fun createLiveticker(liveTicker: LiveTicker): Result<LiveTicker, Exception> {
        return fireStore.collection("livetickers").awaitAddResult(liveTicker)
    }
}