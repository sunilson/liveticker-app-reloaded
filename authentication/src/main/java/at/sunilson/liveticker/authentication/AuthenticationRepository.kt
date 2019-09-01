package at.sunilson.liveticker.authentication

import at.sunilson.liveticker.core.models.User
import at.sunilson.liveticker.firebasecore.generateCompletionListener
import at.sunilson.liveticker.firebasecore.generateResultCompletionListener
import com.github.kittinunf.result.Result
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.suspendCancellableCoroutine

interface IAuthenticationRepository {
    fun observeAuthentication(): ReceiveChannel<User?>
    fun getCurrentUserNow(): Result<User, AuthenticationException>

    suspend fun anonymousLogin(): SuspendableResult<Unit, Exception>
    suspend fun login(email: String, password: String): SuspendableResult<Unit, Exception>
    suspend fun register(email: String, userName: String, password: String): SuspendableResult<Unit, Exception>
    fun logout()
}

internal class FirebaseAuthenticationRepository(private val firebaseAuth: FirebaseAuth) : IAuthenticationRepository {

    override fun getCurrentUserNow(): Result<User, AuthenticationException> {
        val user = firebaseAuth.currentUser
        return if (user == null) Result.error(NotLoggedIn())
        else Result.success(User(user.uid, user.displayName ?: "Anonymous", user.isAnonymous))
    }

    @ExperimentalCoroutinesApi
    override fun observeAuthentication(): ReceiveChannel<User?> {
        val channel = Channel<User?>()

        val listener: FirebaseAuth.AuthStateListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            channel.sendBlocking(
                if (user != null) User(
                    user.uid,
                    user.displayName ?: "Anonymous",
                    user.isAnonymous
                ) else null
            )
        }

        channel.invokeOnClose { firebaseAuth.removeAuthStateListener(listener) }
        firebaseAuth.addAuthStateListener(listener)
        return channel
    }

    override suspend fun anonymousLogin(): SuspendableResult<Unit, Exception> {
        return suspendCancellableCoroutine {
            firebaseAuth.signInAnonymously().addOnCompleteListener(generateCompletionListener(it))
        }
    }

    override suspend fun login(email: String, password: String): SuspendableResult<Unit, Exception> {
        return suspendCancellableCoroutine {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(generateCompletionListener(it))
        }
    }

    override fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun register(email: String, userName: String, password: String): SuspendableResult<Unit, Exception> {
        val (result, error) = suspendCancellableCoroutine<SuspendableResult<AuthResult, Exception>> {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(generateResultCompletionListener(it))
        }

        if (error != null) return SuspendableResult.error(error)
        if (result == null) return SuspendableResult.error(Exception())

        return suspendCancellableCoroutine {
            result
                .user
                .updateProfile(UserProfileChangeRequest.Builder().setDisplayName(userName).build())
                .addOnCompleteListener(generateCompletionListener(it))
        }
    }
}

//TODO
sealed class AuthenticationException : Exception()

class NotLoggedIn : AuthenticationException()