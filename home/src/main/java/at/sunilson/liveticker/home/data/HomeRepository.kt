package at.sunilson.liveticker.home.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.ObservationResult
import at.sunilson.liveticker.firebasecore.awaitDelete
import at.sunilson.liveticker.firebasecore.models.FirebaseLiveticker
import at.sunilson.liveticker.firebasecore.models.convertToDomainEntity
import at.sunilson.liveticker.firebasecore.observeChanges
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow

interface HomeRepository {
    fun getLivetickers(userId: String): Flow<SuspendableResult<List<ObservationResult<LiveTicker>>, Exception>>
    suspend fun deleteLiveticker(id: String): SuspendableResult<Unit, Exception>
}

internal class HomeRepositoryImpl(private val fireStore: FirebaseFirestore) : HomeRepository {

    override fun getLivetickers(userId: String): Flow<SuspendableResult<List<ObservationResult<LiveTicker>>, Exception>> {
        return fireStore
            .collection("livetickers")
            .whereEqualTo("authorId", userId)
            .observeChanges<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }
    }

    override suspend fun deleteLiveticker(id: String): SuspendableResult<Unit, Exception> {
        return fireStore.collection("livetickers").document(id).awaitDelete()
    }
}
