package at.sunilson.liveticker.presentation

import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.presentation.baseClasses.BaseRecyclerAdapter

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
    if (entries != null && entries.isNotEmpty()) {
        val adapter = this.adapter as? BaseRecyclerAdapter<T>
        adapter?.addAll(entries)
    }
}