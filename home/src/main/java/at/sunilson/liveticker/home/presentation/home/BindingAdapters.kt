package at.sunilson.liveticker.home.presentation.home

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import at.sunilson.liveticker.presentation.interfaces.ItemSelectedListener

@BindingAdapter("app:onLivetickerSelected")
fun <T : LivetickerSelectedAction> RecyclerView.onLivetickerSelected(itemSelectedListener: ItemSelectedListener<T>) {
    if (adapter is LivetickerRecyclerAdapter) {
        (adapter as LivetickerRecyclerAdapter).onLivetickerClicked = {
            itemSelectedListener.itemSelected(it as T)
        }
    }
}