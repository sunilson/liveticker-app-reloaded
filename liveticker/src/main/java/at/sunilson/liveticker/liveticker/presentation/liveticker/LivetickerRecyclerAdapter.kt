package at.sunilson.liveticker.liveticker.presentation.liveticker

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter

class LivetickerRecyclerAdapter : BaseRecyclerAdapter<String>(mutableListOf()) {

    override fun getItemViewType(position: Int) = if (position == 0) 0 else 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 0) {
            super.onCreateViewHolder(R.layout.liveticker_text_entry_first, parent)
        } else {
            super.onCreateViewHolder(R.layout.liveticker_text_entry, parent)
        }
    }

    override fun bindViewHolder(binding: ViewDataBinding, obj: String) {}

    override fun unbindViewHolder(binding: ViewDataBinding) {}
}