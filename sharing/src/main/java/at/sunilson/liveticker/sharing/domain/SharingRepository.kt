package at.sunilson.liveticker.sharing.domain

import at.sunilson.liveticker.sharing.domain.models.NoEditUrlFoundException
import com.github.kittinunf.result.coroutines.SuspendableResult

interface SharingRepository {
    suspend fun getEditUrl(livetickerId: String): SuspendableResult<String, NoEditUrlFoundException>
}