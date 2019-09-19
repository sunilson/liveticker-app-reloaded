package at.sunilson.liveticker.liveticker.presentation.liveticker

import android.net.Uri
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.LivetickerGalleryItemBinding
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapterDelegate

class ImageRecyclerDelegate(onItemClicked: (ImageClicked) -> Unit) :
    BaseRecyclerAdapterDelegate<Uri, LivetickerGalleryItemBinding, ImageClicked>(
        R.layout.liveticker_gallery_item,
        onItemClicked
    ) {

    override fun bindViewHolder(
        binding: LivetickerGalleryItemBinding,
        obj: Uri,
        position: Int,
        dataSize: Int,
        onItemClicked: (ImageClicked) -> Unit
    ) {
        binding.root.setOnClickListener { onItemClicked(ImageClicked(obj)) }
        binding.obj = obj
    }

    override fun isForViewType(items: List<Any>, position: Int) = items[position] is Uri
}