package at.sunilson.liveticker.presentation

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.core.padZero
import at.sunilson.liveticker.core.readable
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseDiffRecyclerAdapter
import at.sunilson.liveticker.presentation.baseClasses.recyclerView.BaseRecyclerAdapter
import at.sunilson.liveticker.presentation.interfaces.ItemSelectedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.hannesdorfmann.adapterdelegates4.AbsDelegationAdapter
import org.joda.time.DateTime
import java.io.File

@BindingAdapter("app:timeFromTimestamp")
fun TextView.timeFromTimestamp(time: Long?) {
    if (time != null) {
        val date = DateTime(time)
        text = "${date.hourOfDay.padZero()}:${date.minuteOfHour.padZero()}"
    }
}

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
fun RecyclerView.setEntries(entries: List<Any>?) {
    when {
        entries == null -> return
        adapter is BaseDiffRecyclerAdapter -> (adapter as? BaseDiffRecyclerAdapter)?.items = entries
        adapter is AbsDelegationAdapter<*> -> (adapter as? AbsDelegationAdapter<*>)?.run {
            items = entries
            notifyDataSetChanged()
        }
    }
}

@BindingAdapter("app:onEndIconClicked")
fun <T> TextInputLayout.onEndIconClickedListener(clickListener: View.OnClickListener) {
    setEndIconOnClickListener(clickListener)
}

@BindingAdapter("app:fromPath")
fun ImageView.fromPath(path: String?) {
    if (path == null) return
    val file = File(path)
    if (file.exists() && file.readable()) {
        Glide.with(context)
            .load(Uri.fromFile(file))
            .error(R.drawable.avatar_placeholder)
            .placeholder(R.drawable.ic_add_black_24dp)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

@BindingAdapter("app:fromUrl")
fun ImageView.fromUrl(url: String?) {
    if (url?.isNotEmpty() == true) {
        Glide.with(context).load(url).transition(DrawableTransitionOptions.withCrossFade())
            .into(this)
    }
}

@BindingAdapter("app:fromUri")
fun ImageView.fromUri(uri: Uri?) {
    if (uri == null) return
    Glide.with(context)
        .load(uri)
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(this)
}

@BindingAdapter("app:src")
fun FloatingActionButton.setSrc(drawable: Drawable?) {
    if (drawable == null) return
    setImageDrawable(drawable)
}