package at.sunilson.liveticker.firebasecore.models

import at.sunilson.liveticker.core.models.Comment
import java.util.*

class FirebaseComment(val name: String = "", val comment: String = "", id: String = "") : FirebaseEntity(id)

fun FirebaseComment.convertToDomainEntity() = Comment(name, comment, timestamp ?: Date(), id)
fun Comment.convertToFirebaseEntity() = FirebaseComment(name, comment, id)
