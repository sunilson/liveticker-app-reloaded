package at.sunilson.liveticker.liveticker.presentation.comments

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.core.models.LiveTicker
import at.sunilson.liveticker.liveticker.BR
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter

class CommentsRecyclerAdapter : BaseRecyclerAdapter<Comment>(mutableListOf()) {

    override fun createDiffutilCallback(
        oldList: List<Comment>,
        newList: List<Comment>
    ) = DiffUtilCallback(newList, oldList)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return super.onCreateViewHolder(R.layout.liveticker_comment_entry, parent)
    }

    override fun bindViewHolder(binding: ViewDataBinding, obj: Comment) {
        binding.setVariable(BR.obj, obj)
    }

    override fun unbindViewHolder(binding: ViewDataBinding) {}

    class DiffUtilCallback(private val newList: List<Comment>, private val oldList: List<Comment>) :
        DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size
        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newList[newItemPosition].id == oldList[oldItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return newList[newItemPosition] == oldList[oldItemPosition]
        }
    }
}