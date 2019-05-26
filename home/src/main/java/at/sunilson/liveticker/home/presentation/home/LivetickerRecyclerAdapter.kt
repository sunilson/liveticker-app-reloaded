package at.sunilson.liveticker.home.presentation.home

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.home.R
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter

class LivetickerRecyclerAdapter : BaseRecyclerAdapter<LiveTicker>(mutableListOf(), 1) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onCreateViewHolder(R.layout.liveticker_list_item, parent)
    }
}