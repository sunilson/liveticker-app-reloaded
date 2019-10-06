package at.sunilson.liveticker.presentation.baseClasses.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter
import java.text.FieldPosition

abstract class BaseDiffRecyclerAdapter : BaseRecyclerAdapter() {

    abstract fun areContentsTheSame(
        oldItem: Any,
        newItem: Any,
        oldPosition: Int,
        newPosition: Int
    ): Boolean

    abstract fun areItemsTheSame(
        oldItem: Any,
        newItem: Any,
        oldPosition: Int,
        newPosition: Int
    ): Boolean

    private fun createDiffCallback(oldList: List<Any>, newList: List<Any>) =
        object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                this@BaseDiffRecyclerAdapter.areItemsTheSame(
                    oldList[oldItemPosition],
                    newList[newItemPosition],
                    oldItemPosition,
                    newItemPosition
                )

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int) =
                this@BaseDiffRecyclerAdapter.areContentsTheSame(
                    oldList[oldItemPosition],
                    newList[newItemPosition],
                    oldItemPosition,
                    newItemPosition
                )

            override fun getOldListSize() = oldList.size
            override fun getNewListSize() = newList.size
        }

    override fun setItems(items: List<Any>?) {
        val result =
            DiffUtil.calculateDiff(createDiffCallback(this.items ?: listOf(), items ?: listOf()))
        super.setItems(items)
        result.dispatchUpdatesTo(this)
    }
}