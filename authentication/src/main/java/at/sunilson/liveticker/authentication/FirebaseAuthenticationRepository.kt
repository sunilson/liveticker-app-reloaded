package at.sunilson.liveticker.authentication

import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.firebasecore.generateAddCompletionListener
import com.github.kittinunf.result.Result
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.suspendCancellableCoroutine

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

    override suspend fun login(email: String, password: String): Result<Unit, Exception> {
        return suspendCancellableCoroutine {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(generateAddCompletionListener(it))
        }
    }

    override suspend fun register(email: String, password: String): Result<Unit, Exception> {
        return suspendCancellableCoroutine {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(generateAddCompletionListener(it))
        }
    }
}