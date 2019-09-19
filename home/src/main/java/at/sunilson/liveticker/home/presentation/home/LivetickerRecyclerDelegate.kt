package at.sunilson.liveticker.home.presentation.home

import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.home.R
import at.sunilson.liveticker.home.databinding.LivetickerListItemBinding
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapterDelegate

class LivetickerRecyclerDelegate(onItemClicked: (LivetickerSelectedAction) -> Unit) :
    BaseRecyclerAdapterDelegate<LiveTicker, LivetickerListItemBinding, LivetickerSelectedAction>(
        R.layout.liveticker_list_item,
        onItemClicked
    ) {
    override fun bindViewHolder(
        binding: LivetickerListItemBinding,
        obj: LiveTicker,
        position: Int,
        dataSize: Int,
        onItemClicked: (LivetickerSelectedAction) -> Unit
    ) {
        binding.obj = obj
        binding.root.setOnClickListener { onItemClicked(LivetickerClicked(obj)) }
        binding.livetickerListItemShare.setOnClickListener { onItemClicked(ShareClicked(obj)) }
        binding.livetickerListItemDelete.setOnClickListener { onItemClicked(DeleteClicked(obj)) }
    }

    override fun isForViewType(items: List<Any>, position: Int) = true

}