package at.sunilson.liveticker.authentication

import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.firebasecore.ActionResult
import at.sunilson.liveticker.firebasecore.generateCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.suspendCancellableCoroutine

class FirebaseAuthenticationRepository(private val firebaseAuth: FirebaseAuth) : IAuthenticationRepository {

    override val currentUser: MutableLiveData<FirebaseUser?> = MutableLiveData()

    init {
        firebaseAuth.addAuthStateListener { currentUser.postValue(it.currentUser) }
    }

    override fun getCurrentUserNow() = firebaseAuth.currentUser

    override suspend fun login(email: String, password: String): ActionResult {
        return suspendCancellableCoroutine {
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(generateCompleteListener(it))
        }
    }

    override suspend fun register(email: String, password: String): ActionResult {
        return suspendCancellableCoroutine {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(generateCompleteListener(it))
        }
    }
}