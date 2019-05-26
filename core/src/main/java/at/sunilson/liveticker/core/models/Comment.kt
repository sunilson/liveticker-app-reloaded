package at.sunilson.liveticker.core.models

data class Comment(override var id: String, val name: String, val comment: String): ModelWithId