package at.sunilson.liveticker.liveticker.presentation.photo

import android.net.Uri
import android.view.View
import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel

sealed class PhotoNavigationEvent {
    data class UploadImage(val uri: Uri) : PhotoNavigationEvent()
}

abstract class PhotoViewModel : BaseViewModel<PhotoNavigationEvent>() {
    abstract val currentImage: MutableLiveData<Uri>
    abstract fun loadImage(uri: Uri)
    abstract fun uploadImage(view: View? = null)
}

internal class PhotoViewModelImpl : PhotoViewModel() {
    override val currentImage: MutableLiveData<Uri> = MutableLiveData()

    override fun loadImage(uri: Uri) {
        currentImage.value = uri
    }

    override fun uploadImage(view: View?) {
        navigationEvents.postValue(PhotoNavigationEvent.UploadImage(currentImage.value ?: return))
    }
}

