package at.sunilson.liveticker.presentation.baseClasses

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment : Fragment() {

    /**
     * Sets up the given layout resource with a [BaseViewModel] and data binding
     */
    protected fun <Binding : ViewDataBinding> generateBinding(
        inflater: LayoutInflater,
        @LayoutRes layout: Int,
        container: ViewGroup?
    ): Binding {
        val binding = DataBindingUtil.inflate<Binding>(inflater, layout, container, false)
        binding.lifecycleOwner = this
        return binding
    }
}