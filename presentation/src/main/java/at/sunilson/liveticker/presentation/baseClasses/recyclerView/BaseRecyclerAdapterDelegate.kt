package at.sunilson.liveticker.presentation.baseClasses.recyclerView

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AdapterDelegate

abstract class BaseRecyclerAdapterDelegate<T, Binding : ViewDataBinding, Action>(
    @LayoutRes private val res: Int,
    private val onItemClicked: (Action) -> Unit
) : AdapterDelegate<List<Any>>() {

    override fun onBindViewHolder(
        items: List<Any>,
        position: Int,
        holder: RecyclerView.ViewHolder,
        payloads: MutableList<Any>
    ) {
        (holder as BaseRecyclerAdapterDelegate<T, ViewDataBinding, Action>.BindingViewHolder).bind(
            items[position] as T,
            position,
            items.size
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        val binding = DataBindingUtil.inflate<Binding>(
            LayoutInflater.from(parent.context),
            res,
            parent,
            false
        )

        return BindingViewHolder(binding)
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        super.onViewRecycled(holder)
        if (holder is BaseRecyclerAdapterDelegate<*, *, *>.BindingViewHolder) {
            unbindViewHolder(holder.binding as Binding)
            holder.binding.executePendingBindings()
            holder.binding.unbind()
        }
    }

    abstract fun bindViewHolder(
        binding: Binding,
        obj: T,
        position: Int,
        dataSize: Int,
        onItemClicked: (Action) -> Unit
    )

    open fun unbindViewHolder(binding: Binding) {}

    inner class BindingViewHolder(val binding: Binding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(obj: T, position: Int, dataSize: Int) {
            bindViewHolder(binding, obj, position, dataSize, onItemClicked)
            binding.executePendingBindings()
        }
    }
}