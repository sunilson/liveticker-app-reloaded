package at.sunilson.liveticker.liveticker.presentation

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.liveticker.BR
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter

class CommentsRecyclerAdapter : BaseRecyclerAdapter<Comment>(mutableListOf()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(R.layout.liveticker_comment_entry, parent)
    }

    override fun bindViewHolder(binding: ViewDataBinding, obj: Comment) {
       binding.setVariable(BR.obj, obj)
    }

    override fun unbindViewHolder(binding: ViewDataBinding) {
    }
}