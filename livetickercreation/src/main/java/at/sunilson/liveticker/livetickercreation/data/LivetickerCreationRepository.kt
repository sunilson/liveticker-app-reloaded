package at.sunilson.liveticker.livetickercreation.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.awaitAdd
import at.sunilson.liveticker.firebasecore.models.convertToFirebaseEntity
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.firebase.firestore.FirebaseFirestore

interface LivetickerCreationRepository {
    suspend fun createLiveticker(liveTicker: LiveTicker): SuspendableResult<String, Exception>
}

internal class LivetickerCreationRepositoryImpl(private val fireStore: FirebaseFirestore) :
    LivetickerCreationRepository {
    override suspend fun createLiveticker(liveTicker: LiveTicker): SuspendableResult<String, Exception> {
        return fireStore.collection("livetickers").awaitAdd(liveTicker.convertToFirebaseEntity())
    }
}