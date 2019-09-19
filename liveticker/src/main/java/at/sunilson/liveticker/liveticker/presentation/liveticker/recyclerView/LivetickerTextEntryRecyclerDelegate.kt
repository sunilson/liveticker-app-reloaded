package at.sunilson.liveticker.liveticker.presentation.liveticker.recyclerView

import at.sunilson.liveticker.core.models.TextLiveTickerEntry
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.LivetickerTextEntryBinding
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapterDelegate

class LivetickerTextEntryRecyclerDelegate(onItemClicked: (LivetickerEntryClicked) -> Unit) :
    BaseRecyclerAdapterDelegate<TextLiveTickerEntry, LivetickerTextEntryBinding, LivetickerEntryClicked>(
        R.layout.liveticker_text_entry,
        onItemClicked
    ) {
    override fun bindViewHolder(
        binding: LivetickerTextEntryBinding,
        obj: TextLiveTickerEntry,
        position: Int,
        dataSize: Int,
        onItemClicked: (LivetickerEntryClicked) -> Unit
    ) {

    }

    override fun isForViewType(items: List<Any>, position: Int) = true
}