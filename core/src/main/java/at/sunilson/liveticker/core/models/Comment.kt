package at.sunilson.liveticker.core.models

data class Comment(val name: String = "", val comment: String = "", override var id: String = "") : ModelWithId