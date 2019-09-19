package at.sunilson.liveticker.liveticker.domain

import android.app.Application
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import at.sunilson.liveticker.core.usecases.UseCase
import com.github.kittinunf.result.Result
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CreateImageFileUseCase(
    private val context: Context,
    private val fileProviderName: String
) :
    UseCase<Uri, Unit>() {
    override fun run(params: Unit) = Result.of<Uri, Exception> {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        File.createTempFile(
            "JPEG_${timeStamp}",
            ".jpg",
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        ).let {
            FileProvider.getUriForFile(context, fileProviderName, it)
        }
    }
}