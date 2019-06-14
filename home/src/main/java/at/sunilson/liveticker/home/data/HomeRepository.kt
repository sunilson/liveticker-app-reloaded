package at.sunilson.liveticker.home.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.awaitAddResult
import at.sunilson.liveticker.firebasecore.observe
import com.github.kittinunf.result.Result
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ReceiveChannel

interface HomeRepository {
    fun getLivetickers(userId: String): ReceiveChannel<List<LiveTicker>>
}

internal class HomeRepositoryImpl(private val fireStore: FirebaseFirestore) : HomeRepository {

    override fun getLivetickers(userId: String): ReceiveChannel<List<LiveTicker>> {
        return fireStore
            .collection("livetickers")
            .whereEqualTo("authorId", userId)
            .observe()
    }

}
