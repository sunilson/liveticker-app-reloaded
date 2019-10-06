package at.sunilson.liveticker.liveticker.presentation.liveticker.recyclerView

import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.LivetickerTextEntryBinding
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapterDelegate

class LivetickerTextEntryRecyclerDelegate(onItemClicked: (LivetickerEntryClicked) -> Unit) :
    BaseRecyclerAdapterDelegate<LiveTickerEntry.TextLiveTickerEntry, LivetickerTextEntryBinding, LivetickerEntryClicked>(
        R.layout.liveticker_text_entry,
        onItemClicked
    ) {
    override fun bindViewHolder(
        binding: LivetickerTextEntryBinding,
        obj: LiveTickerEntry.TextLiveTickerEntry,
        position: Int,
        dataSize: Int,
        onItemClicked: (LivetickerEntryClicked) -> Unit
    ) {
        binding.last = position == 0
        binding.first = position == dataSize - 1
    }

    override fun isForViewType(items: List<Any>, position: Int) = items[position] is LiveTickerEntry.TextLiveTickerEntry
}