package at.sunilson.liveticker.presentation.baseClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T>(val data: MutableList<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClicked: (View) -> Unit = {}

    open fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    open fun addAll(elements: List<T>) {
        val diffutilCallback = createDiffutilCallback(data, elements)

        if (diffutilCallback != null) {
            val diffResult = DiffUtil.calculateDiff(diffutilCallback)
            diffResult.dispatchUpdatesTo(this)
            this.data.clear()
            this.data.addAll(elements)
        } else {
            val size = this.data.size
            this.data.clear()
            notifyItemRangeRemoved(0, size)
            this.data.addAll(elements)
            notifyItemRangeInserted(0, data.size)
        }
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is BaseRecyclerAdapter<*>.BindingViewHolder) {
            (holder as BaseRecyclerAdapter<T>.BindingViewHolder).bind(data[position])
        }
    }

    protected fun onCreateViewHolder(@LayoutRes res: Int, parent: ViewGroup): RecyclerView.ViewHolder {
        val binding: ViewDataBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            res,
            parent,
            false
        )

        binding.root.setOnClickListener { onItemClicked(it) }
        return BindingViewHolder(binding)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BaseRecyclerAdapter<*>.BindingViewHolder) {
            unbindViewHolder(holder.binding)
            holder.binding.executePendingBindings()
        }
    }

    open fun createDiffutilCallback(oldList: List<T>, newList: List<T>): DiffUtil.Callback? = null
    abstract fun bindViewHolder(binding: ViewDataBinding, obj: T)
    abstract fun unbindViewHolder(binding: ViewDataBinding)

    inner class BindingViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: T) {
            bindViewHolder(binding, obj)
            binding.executePendingBindings()
        }
    }
}