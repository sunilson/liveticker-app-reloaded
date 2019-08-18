package at.sunilson.liveticker.presentation

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter
import at.sunilson.liveticker.presentation.interfaces.ItemSelectedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions

@BindingAdapter("app:hideIfNull")
fun View.hideIfNull(obj: Any?) {
    visibility = if (obj == null) {
        GONE
    } else {
        VISIBLE
    }
}

@BindingAdapter("app:showIf")
fun View.show(value: Boolean?) {
    visibility = if (value == true) {
        VISIBLE
    } else {
        GONE
    }
}

@BindingAdapter("app:hideIf")
fun View.hide(value: Boolean?) {
    visibility = if (value == true) {
        GONE
    } else {
        VISIBLE
    }
}

@BindingAdapter("app:entries")
fun <T> RecyclerView.setEntries(entries: List<T>?) {
    if (entries != null) {
        val adapter = this.adapter as? BaseRecyclerAdapter<T>
        adapter?.addAll(entries)
    }
}

@BindingAdapter("app:onItemSelected")
fun <T> RecyclerView.onItemSelected(itemSelectedListener: ItemSelectedListener<T>) {
    if (adapter is BaseRecyclerAdapter<*>) {
        (adapter as BaseRecyclerAdapter<T>).onItemClicked = {
            itemSelectedListener.itemSelected((adapter as BaseRecyclerAdapter<T>).data[getChildLayoutPosition(it)])
        }
    }
}

@BindingAdapter("app:fromUrl")
fun ImageView.fromUrl(url: String?) {
    if (url?.isNotEmpty() == true) {
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade()).into(this)
    }
}