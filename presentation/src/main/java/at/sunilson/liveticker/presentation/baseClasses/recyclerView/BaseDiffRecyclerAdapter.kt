package at.sunilson.liveticker.presentation.baseClasses.recyclerView

import androidx.recyclerview.widget.DiffUtil
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

abstract class BaseDiffRecyclerAdapter(diffCallback: DiffUtil.ItemCallback<Any>) :
    AsyncListDifferDelegationAdapter<Any>(diffCallback)