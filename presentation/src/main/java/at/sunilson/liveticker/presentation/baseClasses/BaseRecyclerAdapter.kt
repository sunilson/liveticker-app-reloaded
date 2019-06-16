package at.sunilson.liveticker.presentation.baseClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerAdapter<T>(val data: MutableList<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var onItemClicked: (View) -> Unit = {}

    open fun clear() {
        data.clear()
        notifyDataSetChanged()
    }

    open fun addAll(elements: List<T>) {
        this.data.clear()
        this.data.addAll(elements)
        notifyDataSetChanged()
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
}