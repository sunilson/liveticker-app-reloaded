package at.sunilson.liveticker.liveticker.domain

import at.sunilson.liveticker.core.usecases.AsyncUseCase
import com.github.kittinunf.result.coroutines.SuspendableResult
import android.R
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.File


class GetLocalImagesUseCase(private val repository: LivetickerRepository) :
    AsyncUseCase<List<Uri>, Exception, Unit>() {
    override suspend fun run(params: Unit) = repository.getLocalImagePaths()
}