package at.sunilson.liveticker.presentation.baseClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import at.sunilson.liveticker.presentation.R
import at.sunilson.liveticker.presentation.interfaces.BackpressInterceptor
import timber.log.Timber

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

    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {

        //Show above previous fragment for animation purposes
        if (nextAnim == R.anim.move_in_from_right) {
            ViewCompat.setTranslationZ(view!!, 200f)
        } else {
            ViewCompat.setTranslationZ(view!!, 100f)
        }

        return super.onCreateAnimation(transit, enter, nextAnim)
    }
}