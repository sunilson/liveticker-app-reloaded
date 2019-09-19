package at.sunilson.liveticker.liveticker.presentation.photo

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import at.sunilson.liveticker.presentation.baseClasses.BaseViewModel

sealed class PhotoNavigationEvent {

}

abstract class PhotoViewModel : BaseViewModel<PhotoNavigationEvent>() {
    abstract val currentImage: MutableLiveData<Uri>
    abstract fun loadImage(uri: Uri)
}

internal class PhotoViewModelImpl : PhotoViewModel() {
    override val currentImage: MutableLiveData<Uri> = MutableLiveData()

    override fun loadImage(uri: Uri) {
        currentImage.value = uri
    }
}

