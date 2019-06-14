package at.sunilson.liveticker.liveticker.data

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.firebasecore.observe
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.ReceiveChannel

interface LivetickerRepository {
    fun getLiveTicker(id: String): ReceiveChannel<LiveTicker>
}

class LiveTickerRepositoryImpl(private val fireStore: FirebaseFirestore) : LivetickerRepository {

    override fun getLiveTicker(id: String): ReceiveChannel<LiveTicker> {
        return fireStore
            .document("livetickers/$id")
            .observe()
    }
}