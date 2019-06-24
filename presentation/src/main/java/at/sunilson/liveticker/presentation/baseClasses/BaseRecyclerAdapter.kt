package at.sunilson.liveticker.presentation.baseClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T>(val data: MutableList<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClicked: (View) -> Unit = {}
    var callback = ObservableCallback()

    open fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    fun addObservableList(list: ObservableList<T>) {
        val size = this.data.size
        data.clear()
        notifyItemRangeRemoved(0, size)
        this.data.addAll(list)
        notifyItemRangeInserted(0, data.size)
        list.addOnListChangedCallback(callback)
    }

    open fun addAll(elements: List<T>) {
        val size = this.data.size
        this.data.clear()
        notifyItemRangeRemoved(0, size)
        this.data.addAll(elements)
        notifyItemRangeInserted(0, data.size)
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

    abstract fun bindViewHolder(binding: ViewDataBinding, obj: T)
    abstract fun unbindViewHolder(binding: ViewDataBinding)

    inner class BindingViewHolder(val binding: ViewDataBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: T) {
            bindViewHolder(binding, obj)
            binding.executePendingBindings()
        }
    }

    inner class ObservableCallback : ObservableList.OnListChangedCallback<ObservableList<T>>() {
        override fun onChanged(sender: ObservableList<T>?) {
        }

        override fun onItemRangeRemoved(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
            if(sender == null) return
            data.clear()
            data.addAll(sender)
            notifyItemRangeRemoved(positionStart, itemCount)
        }

        override fun onItemRangeMoved(sender: ObservableList<T>?, fromPosition: Int, toPosition: Int, itemCount: Int) {}

        override fun onItemRangeInserted(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
            if(sender == null) return
            data.clear()
            data.addAll(sender)
            notifyItemRangeInserted(positionStart, itemCount)
        }

        override fun onItemRangeChanged(sender: ObservableList<T>?, positionStart: Int, itemCount: Int) {
            if(sender == null) return
            data.clear()
            data.addAll(sender)
            notifyItemRangeChanged(positionStart, itemCount)
        }
    }
}