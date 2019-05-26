package at.sunilson.liveticker.core.models

import java.util.*

class LiveTicker(
    override var id: String = "",
    val authorId: String = "",
    val title: String = "",
    val organizer: String = "",
    val plannedStartDate: Date = Date(),
    val finishedDate: Date = Date(),
    val imageUrl: String = "",
    val started: Boolean = false,
    val finished: Boolean = false,
    val location: Pair<Long, Long>? = null
): ModelWithId {}