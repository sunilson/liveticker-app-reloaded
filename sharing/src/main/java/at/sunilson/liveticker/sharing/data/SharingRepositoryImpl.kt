package at.sunilson.liveticker.sharing.data

import at.sunilson.liveticker.firebasecore.awaitGet
import at.sunilson.liveticker.sharing.data.entities.EditUrl
import at.sunilson.liveticker.sharing.domain.SharingRepository
import at.sunilson.liveticker.sharing.domain.models.NoEditUrlFoundException
import com.github.kittinunf.result.coroutines.SuspendableResult
import com.github.kittinunf.result.coroutines.map
import com.github.kittinunf.result.coroutines.mapError
import com.google.firebase.firestore.FirebaseFirestore

internal class SharingRepositoryImpl(private val firestore: FirebaseFirestore) : SharingRepository {
    override suspend fun getEditUrl(livetickerId: String): SuspendableResult<String, NoEditUrlFoundException> {
        return firestore
            .collection("editUrls")
            .document(livetickerId)
            .awaitGet<EditUrl>()
            .mapError { NoEditUrlFoundException() }
            .map { it.editUrl }
    }
}