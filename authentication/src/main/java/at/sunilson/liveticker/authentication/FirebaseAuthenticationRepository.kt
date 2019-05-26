package at.sunilson.liveticker.authentication

import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.firebasecore.ActionResult
import at.sunilson.liveticker.firebasecore.generateCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

internal class FirebaseAuthenticationRepository(private val firebaseAuth: FirebaseAuth) : IAuthenticationRepository {

    override val currentUser: MutableLiveData<User?> = MutableLiveData()

    init {
        firebaseAuth.addAuthStateListener {
            val user = it.currentUser
            currentUser.postValue(if (user != null) User(user.uid) else null)
        }
    }

    override fun getCurrentUserNow(): User? {
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) return User(currentUser.uid)
        return null
    }

    override suspend fun login(email: String, password: String): ActionResult {
        return suspendCancellableCoroutine {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(generateCompleteListener(it))
        }
    }

    override suspend fun register(email: String, password: String): ActionResult {
        return suspendCancellableCoroutine {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(generateCompleteListener(it))
        }
    }
}