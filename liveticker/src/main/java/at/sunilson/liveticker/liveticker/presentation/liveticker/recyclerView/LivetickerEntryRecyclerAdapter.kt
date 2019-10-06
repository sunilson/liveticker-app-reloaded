package at.sunilson.liveticker.liveticker.presentation.liveticker.recyclerView

import at.sunilson.liveticker.core.models.LiveTickerEntry
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseDiffRecyclerAdapter

data class LivetickerEntryClicked(val entry: LiveTickerEntry)

class LivetickerEntryRecyclerAdapter(onItemClicked: (LivetickerEntryClicked) -> Unit) :
    BaseDiffRecyclerAdapter() {

    init {
        delegatesManager
            .addDelegate(LivetickerTextEntryRecyclerDelegate(onItemClicked))
            .addDelegate(LivetickerImageEntryRecyclerDelegate(onItemClicked))
    }

    override fun areContentsTheSame(
        oldItem: Any,
        newItem: Any,
        oldPosition: Int,
        newPosition: Int
    ) = when {
        firstOrLastChange(oldPosition, newPosition) -> false
        oldItem is LiveTickerEntry.TextLiveTickerEntry && newItem is LiveTickerEntry.TextLiveTickerEntry -> newItem == oldItem
        oldItem is LiveTickerEntry.ImageLivetickerEntry && newItem is LiveTickerEntry.ImageLivetickerEntry -> newItem == oldItem
        else -> false
    }

    override fun areItemsTheSame(
        oldItem: Any,
        newItem: Any,
        oldPosition: Int,
        newPosition: Int
    ) = when {
        firstOrLastChange(oldPosition, newPosition) -> false
        oldItem is LiveTickerEntry.TextLiveTickerEntry && newItem is LiveTickerEntry.TextLiveTickerEntry -> newItem.id == oldItem.id
        oldItem is LiveTickerEntry.ImageLivetickerEntry && newItem is LiveTickerEntry.ImageLivetickerEntry -> newItem.id == oldItem.id
        else -> false
    }

    private fun firstOrLastChange(oldPosition: Int, newPosition: Int) =
        (oldPosition == 0 && newPosition != 0) ||
                (oldPosition != 0 && newPosition == 0) ||
                (oldPosition == items.size - 1 && newPosition != items.size - 1) ||
                (oldPosition != items.size - 1 && newPosition == items.size - 1)

}

