package at.sunilson.liveticker.livetickercreation.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.awaitAdd
import com.github.kittinunf.result.Result
import com.google.firebase.firestore.FirebaseFirestore

interface LivetickerCreationRepository {
    suspend fun createLiveticker(liveTicker: LiveTicker): Result<String, Exception>
}

internal class LivetickerCreationRepositoryImpl(private val fireStore: FirebaseFirestore) : LivetickerCreationRepository {
    override suspend fun createLiveticker(liveTicker: LiveTicker): Result<String, Exception> {
        return fireStore.collection("livetickers").awaitAdd(liveTicker)
    }
}