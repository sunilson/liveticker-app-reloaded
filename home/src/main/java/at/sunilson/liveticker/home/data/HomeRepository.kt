package at.sunilson.liveticker.home.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.ObservationResult
import at.sunilson.liveticker.firebasecore.awaitDelete
import at.sunilson.liveticker.firebasecore.observe
import at.sunilson.liveticker.firebasecore.observeChanges
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ReceiveChannel

interface HomeRepository {
    fun getLivetickers(userId: String): ReceiveChannel<SuspendableResult<ObservationResult<LiveTicker>, Exception>>
    suspend fun deleteLiveticker(id: String): SuspendableResult<Unit, Exception>
}

internal class HomeRepositoryImpl(private val fireStore: FirebaseFirestore) : HomeRepository {

    override fun getLivetickers(userId: String): ReceiveChannel<SuspendableResult<ObservationResult<LiveTicker>, Exception>> {
        return fireStore
            .collection("livetickers")
            .whereEqualTo("authorId", userId)
            .observeChanges()
    }

    override suspend fun deleteLiveticker(id: String): SuspendableResult<Unit, Exception> {
        return fireStore.collection("livetickers").document(id).awaitDelete()
    }
}
