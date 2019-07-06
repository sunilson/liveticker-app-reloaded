package at.sunilson.liveticker.core.models

data class Comment(val name: String = "", val comment: String = "", val timestamp: Long,  override var id: String = "") : ModelWithId