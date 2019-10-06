package at.sunilson.liveticker.liveticker.presentation.liveticker.recyclerView

import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.LivetickerImageEntryBinding
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapterDelegate

class LivetickerImageEntryRecyclerDelegate(onItemClicked: (LivetickerEntryClicked) -> Unit) :
    BaseRecyclerAdapterDelegate<LiveTickerEntry.ImageLivetickerEntry, LivetickerImageEntryBinding, LivetickerEntryClicked>(
        R.layout.liveticker_image_entry,
        onItemClicked
    ) {
    override fun bindViewHolder(
        binding: LivetickerImageEntryBinding,
        obj: LiveTickerEntry.ImageLivetickerEntry,
        position: Int,
        dataSize: Int,
        onItemClicked: (LivetickerEntryClicked) -> Unit
    ) {
        binding.last = position == 0
        binding.first = position == dataSize - 1
        binding.entry = obj
    }

    override fun isForViewType(items: List<Any>, position: Int) = items[position] is LiveTickerEntry.ImageLivetickerEntry
}