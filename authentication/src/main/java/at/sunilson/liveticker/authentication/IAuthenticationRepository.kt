package at.sunilson.liveticker.authentication

import androidx.lifecycle.LiveData
import at.sunilson.liveticker.firebasecore.ActionResult
import com.google.firebase.auth.FirebaseUser

interface IAuthenticationRepository {
    val currentUser: LiveData<User?>

    fun getCurrentUserNow(): User?
    suspend fun login(email: String, password: String): ActionResult
    suspend fun register(email: String, password: String): ActionResult
}