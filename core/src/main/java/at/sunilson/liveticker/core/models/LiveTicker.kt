package at.sunilson.liveticker.core.models

import java.text.SimpleDateFormat
import java.util.*

class LiveTicker(
    override var id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val title: String = "",
    val shortDescription: String = "",
    val description: String = "",
    val creationDate: Date? = Date(),
    val plannedStartDate: Date? = null,
    val finishedDate: Date? = null,
    val started: Boolean = false,
    val finished: Boolean = false,
    val location: Location? = null
) : ModelWithId {
    val formattedCreationDate: String
        get() {
            return SimpleDateFormat("dd.mm.YYYY", Locale.getDefault()).format(creationDate)
        }
}