package at.sunilson.liveticker.liveticker.presentation

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter

class LivetickerRecyclerAdapter : BaseRecyclerAdapter<String>(mutableListOf()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(R.layout.liveticker_text_entry, parent)
    }

    override fun bindViewHolder(binding: ViewDataBinding, obj: String) {
    }

    override fun unbindViewHolder(binding: ViewDataBinding) {
    }
}