package at.sunilson.liveticker.home.presentation.home

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseDiffRecyclerAdapter

class LivetickerRecyclerAdapter(onItemClicked: (LivetickerSelectedAction) -> Unit) :
    BaseDiffRecyclerAdapter() {
    init {
        delegatesManager.addDelegate(LivetickerRecyclerDelegate(onItemClicked))
    }

    override fun areContentsTheSame(
        oldItem: Any,
        newItem: Any,
        oldPosition: Int,
        newPosition: Int
    ) =
        if (oldItem is LiveTicker && newItem is LiveTicker) newItem == oldItem
        else false

    override fun areItemsTheSame(
        oldItem: Any,
        newItem: Any,
        oldPosition: Int,
        newPosition: Int
    ) =
        if (oldItem is LiveTicker && newItem is LiveTicker) newItem.id == oldItem.id
        else false
}