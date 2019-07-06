package at.sunilson.liveticker.core.models

import java.util.*

data class Comment(
    val name: String = "",
    val comment: String = "",
    val creationDate: Date,
    override var id: String = ""
) : ModelWithId