package at.sunilson.liveticker.firebasecore.models

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.core.models.Location
import com.google.firebase.firestore.FieldValue
import java.util.*

class FirebaseLiveticker(
    val authorId: String = "",
    val authorName: String = "",
    val title: String = "",
    val shortDescription: String = "",
    val description: String = "",
    val plannedStartDate: Date? = null,
    val started: Boolean = false,
    val finished: Boolean = false,
    val location: Location? = null,
    val cheers: Int = 0,
    val sharingUrl: String = "",
    id: String = ""
) : FirebaseEntity(id)

fun FirebaseLiveticker.convertToDomainEntity() = LiveTicker(
    id,
    authorId,
    authorName,
    title,
    shortDescription,
    description,
    timestamp ?: Date(),
    sharingUrl,
    plannedStartDate,
    started,
    finished,
    location
)

fun LiveTicker.convertToFirebaseEntity() = FirebaseLiveticker(
    authorId,
    authorName,
    title,
    shortDescription,
    description,
    plannedStartDate,
    started,
    finished,
    location,
    cheers,
    id
)
