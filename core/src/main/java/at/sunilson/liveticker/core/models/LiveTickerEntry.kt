package at.sunilson.liveticker.core.models

import java.util.*

data class LiveTickerEntry(
    override var id: String,
    val reaction: String,
    val time: Date,
    val text: String,
    val image: String?,
    val style: Int = 0
): ModelWithId