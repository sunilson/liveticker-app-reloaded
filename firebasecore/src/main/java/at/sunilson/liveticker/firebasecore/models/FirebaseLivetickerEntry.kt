package at.sunilson.liveticker.firebasecore.models

import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.firebasecore.FirebaseEntity

class FirebaseLivetickerEntry(
    val text: String? = null,
    val image: String? = null,
    val caption: String? = null,
    id: String = ""
) : FirebaseEntity(id)

fun FirebaseLivetickerEntry.convertToDomainEntity() = if (text != null) {
    LiveTickerEntry.TextLiveTickerEntry(id, timestamp?.time ?: System.currentTimeMillis(), "", text)
} else {
    LiveTickerEntry.ImageLivetickerEntry(
        id,
        timestamp?.time ?: System.currentTimeMillis(),
        caption ?: "",
        image
    )
}

fun LiveTickerEntry.convertToFirebaseEntity() = if (this is LiveTickerEntry.TextLiveTickerEntry) {
    FirebaseLivetickerEntry(text, id)
} else  {
    val imageEntity = this as LiveTickerEntry.ImageLivetickerEntry
    FirebaseLivetickerEntry(null, image, caption, id)
}