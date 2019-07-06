package at.sunilson.liveticker.firebasecore.models

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import java.util.*

abstract class FirebaseEntity(@Exclude val id: String, @ServerTimestamp val timestamp: Date? = null)