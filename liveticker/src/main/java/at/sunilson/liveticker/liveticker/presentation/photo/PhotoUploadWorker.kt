package at.sunilson.liveticker.liveticker.presentation.photo

import android.content.Context
import android.net.Uri
import androidx.work.*
import at.sunilson.liveticker.liveticker.domain.PostLivetickerImageEntry
import org.koin.core.KoinComponent
import org.koin.core.inject

class PhotoUploadWorker(context: Context, workerParams: WorkerParameters) :
    CoroutineWorker(context, workerParams), KoinComponent {

    override suspend fun doWork(): Result {
        val postLivetickerImageEntry: PostLivetickerImageEntry by inject()

        val livetickerId = checkNotNull(inputData.getString(LIVETICKER_ID))
        val caption = checkNotNull(inputData.getString(CAPTION))
        val uri = checkNotNull(Uri.parse(inputData.getString(URI)))

        val (result, error) = postLivetickerImageEntry(
            PostLivetickerImageEntry.Params(
                livetickerId,
                caption,
                uri
            )
        )

        return if (error != null) {
            Result.failure()
        } else {
            Result.success()
        }
    }

    companion object {
        const val LIVETICKER_ID = "livetickerId"
        const val CAPTION = "caption"
        const val URI = "uri"
    }
}

interface PhotoUploadWorkerFactory {
    fun create(data: Data, tag: String): OneTimeWorkRequest
}

internal class PhotoUploadWorkerFactoryImpl : PhotoUploadWorkerFactory {
    override fun create(data: Data, tag: String) = OneTimeWorkRequestBuilder<PhotoUploadWorker>()
        .setInputData(data)
        .addTag(tag)
        .build()
}