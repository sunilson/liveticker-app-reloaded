package at.sunilson.liveticker.core.models

import java.util.*

data class LiveTicker(
    val id: String,
    val authorId: String,
    val title: String,
    val organizer: String,
    val plannedStartDate: Date,
    val finishedDate: Date,
    val imageUrl: String,
    val started: Boolean,
    val finished: Boolean
)