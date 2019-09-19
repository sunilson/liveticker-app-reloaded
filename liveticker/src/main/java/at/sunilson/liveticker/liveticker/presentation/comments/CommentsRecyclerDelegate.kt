package at.sunilson.liveticker.liveticker.presentation.comments

import at.sunilson.liveticker.core.models.Comment
import at.sunilson.liveticker.liveticker.R
import at.sunilson.liveticker.liveticker.databinding.LivetickerCommentEntryBinding
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapterDelegate

class CommentsRecyclerDelegate(onItemClicked: (CommentClicked) -> Unit) :
    BaseRecyclerAdapterDelegate<Comment, LivetickerCommentEntryBinding, CommentClicked>(
        R.layout.liveticker_comment_entry,
        onItemClicked
    ) {

    override fun bindViewHolder(
        binding: LivetickerCommentEntryBinding,
        obj: Comment,
        position: Int,
        dataSize: Int,
        onItemClicked: (CommentClicked) -> Unit
    ) {
        binding.obj = obj
    }

    override fun isForViewType(items: List<Any>, position: Int) = true
}