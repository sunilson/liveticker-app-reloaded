package at.sunilson.liveticker.liveticker.presentation.liveticker

import android.net.Uri
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.liveticker.BR
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapter

data class ImageClicked(val uri: Uri)

class ImageRecyclerAdapter(onClicked: (ImageClicked) -> Unit) :
    BaseRecyclerAdapter() {
    init {
        delegatesManager.addDelegate(ImageRecyclerDelegate(onClicked))
    }
}