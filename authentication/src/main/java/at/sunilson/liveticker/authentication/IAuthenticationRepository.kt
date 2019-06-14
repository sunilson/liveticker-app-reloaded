package at.sunilson.liveticker.authentication

import androidx.lifecycle.LiveData
import com.github.kittinunf.result.Result

interface IAuthenticationRepository {
    val currentUser: LiveData<User?>

    fun getCurrentUserNow(): User?
    suspend fun login(email: String, password: String): Result<Unit, Exception>
    suspend fun register(email: String, password: String): Result<Unit, Exception>
}