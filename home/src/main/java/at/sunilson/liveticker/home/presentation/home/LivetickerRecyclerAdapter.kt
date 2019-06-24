package at.sunilson.liveticker.home.presentation.home

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.home.BR
import at.sunilson.liveticker.home.R
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter
import kotlinx.android.synthetic.main.liveticker_list_item.view.*

class LivetickerRecyclerAdapter : BaseRecyclerAdapter<LiveTicker>(mutableListOf()) {

    var onLivetickerClicked: (LivetickerSelectedAction) -> Unit = {}

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val liveTicker = data[position]

        holder.itemView.setOnClickListener { onLivetickerClicked(LivetickerClicked(liveTicker)) }
        holder.itemView.liveticker_list_item_share.setOnClickListener { onLivetickerClicked(ShareClicked(liveTicker)) }
        holder.itemView.liveticker_list_item_delete.setOnClickListener { onLivetickerClicked(DeleteClicked(liveTicker)) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateViewHolder(R.layout.liveticker_list_item, parent)
    }

    override fun bindViewHolder(binding: ViewDataBinding, obj: LiveTicker) {
        binding.setVariable(BR.obj, obj)
    }

    override fun unbindViewHolder(binding: ViewDataBinding) {
        binding.setVariable(BR.obj, null)
    }
}