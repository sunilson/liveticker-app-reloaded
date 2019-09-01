package at.sunilson.liveticker.home.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.awaitDelete
import at.sunilson.liveticker.firebasecore.models.FirebaseLiveticker
import at.sunilson.liveticker.firebasecore.models.convertToDomainEntity
import at.sunilson.liveticker.firebasecore.observe
import at.sunilson.liveticker.home.domain.HomeRepository
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

internal class HomeRepositoryImpl(private val fireStore: FirebaseFirestore) : HomeRepository {

    @ExperimentalCoroutinesApi
    override fun getLivetickers(userId: String): Flow<SuspendableResult<List<LiveTicker>, Exception>> {
        return fireStore
            .collection("livetickers")
            .whereEqualTo("authorId", userId)
            .observe<FirebaseLiveticker, LiveTicker> { it.convertToDomainEntity() }
    }

    override suspend fun deleteLiveticker(id: String): SuspendableResult<Unit, Exception> {
        return fireStore.collection("livetickers").document(id).awaitDelete()
    }
}
