package at.sunilson.liveticker.liveticker.presentation.comments

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.liveticker.BR
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapter

data class CommentClicked(val comment: Comment)

class CommentsRecyclerAdapter(onItemClicked: (CommentClicked) -> Unit) : BaseRecyclerAdapter() {
    init {
        delegatesManager.addDelegate(CommentsRecyclerDelegate(onItemClicked))
    }
}