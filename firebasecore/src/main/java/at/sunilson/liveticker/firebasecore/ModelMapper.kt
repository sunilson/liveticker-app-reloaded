package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.firebasecore.models.FirebaseComment

fun Comment.asFirebaseModel(): FirebaseComment {
    return FirebaseComment(this.name, this.comment)
}

fun FirebaseComment.asDomainModel(id: String): Comment {
    return Comment(this.name, this.comment, this.timestamp!!.time, id)
}