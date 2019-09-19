package at.sunilson.liveticker.home.presentation.home

import androidx.recyclerview.widget.DiffUtil
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseDiffRecyclerAdapter

class LivetickerRecyclerAdapter(onItemClicked: (LivetickerSelectedAction) -> Unit) :
    BaseDiffRecyclerAdapter(LivetickerDiffUtilCallback()) {
    init {
        delegatesManager.addDelegate(LivetickerRecyclerDelegate(onItemClicked))
    }
}

class LivetickerDiffUtilCallback : DiffUtil.ItemCallback<Any>() {
    override fun areContentsTheSame(oldItem: Any, newItem: Any) =
        if (oldItem is LiveTicker && newItem is LiveTicker) newItem == oldItem
        else false

    override fun areItemsTheSame(oldItem: Any, newItem: Any) =
        if (oldItem is LiveTicker && newItem is LiveTicker) newItem.id == oldItem.id
        else false
}