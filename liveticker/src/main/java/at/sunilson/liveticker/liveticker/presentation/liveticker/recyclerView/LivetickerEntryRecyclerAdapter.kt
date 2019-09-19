package at.sunilson.liveticker.liveticker.presentation.liveticker.recyclerView

import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapter

data class LivetickerEntryClicked(val entry: LiveTickerEntry)

class LivetickerEntryRecyclerAdapter(onItemClicked: (LivetickerEntryClicked) -> Unit) : BaseRecyclerAdapter() {

    init {
        delegatesManager.addDelegate(LivetickerTextEntryRecyclerDelegate(onItemClicked))
    }
}