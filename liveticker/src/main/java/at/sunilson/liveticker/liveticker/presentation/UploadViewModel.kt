package at.sunilson.liveticker.liveticker.presentation

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.work.Data
import androidx.work.WorkInfo
import androidx.work.WorkManager
import at.sunilson.liveticker.liveticker.presentation.photo.PhotoUploadWorker
import at.sunilson.liveticker.liveticker.presentation.photo.PhotoUploadWorkerFactory
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel
import org.koin.core.KoinComponent

sealed class UploadViewModelEvents {

}

abstract class UploadViewModel : BaseViewModel<UploadViewModelEvents>() {
    abstract fun uploadImageToLiveticker(livetickerId: String, uri: Uri, caption: String)
    abstract fun getRunningWorkForLiveticker(livetickerId: String): LiveData<List<WorkInfo>>
}

internal class UploadViewModelImpl(
    private val workManager: WorkManager,
    private val photoUploadWorkerFactory: PhotoUploadWorkerFactory
) : UploadViewModel(), KoinComponent {

    override fun uploadImageToLiveticker(livetickerId: String, uri: Uri, caption: String) {
        val data = Data
            .Builder()
            .putString(PhotoUploadWorker.LIVETICKER_ID, livetickerId)
            .putString(PhotoUploadWorker.CAPTION, caption)
            .putString(PhotoUploadWorker.URI, uri.toString())
            .build()
        val request = photoUploadWorkerFactory.create(data, livetickerId)
        workManager.enqueue(request)
    }

    override fun getRunningWorkForLiveticker(livetickerId: String) =
        workManager.getWorkInfosByTagLiveData(livetickerId)

}