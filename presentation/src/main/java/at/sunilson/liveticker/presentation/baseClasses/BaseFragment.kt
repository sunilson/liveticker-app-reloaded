package at.sunilson.liveticker.presentation.baseClasses

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
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

    /*
    fun backPressed(): Boolean {
        if (handleBackpress()) {
            Timber.d("Back press was handled by $this")
            return true
        }

        childFragmentManager.fragments.forEach {
            if (it is BackpressInterceptor) {
                if (it.handleBackpress()) {
                    Timber.d("Back press was handled by $it")
                    return true
                }
            }
        }

        return false
    }
    */

    //Add hardware layer to improve transition animation performance
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {
        var animation = super.onCreateAnimation(transit, enter, nextAnim)

        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(activity, nextAnim)
        }

        if (animation != null) {
            view?.setLayerType(View.LAYER_TYPE_HARDWARE, null)


            animation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    view?.setLayerType(View.LAYER_TYPE_NONE, null)
                }
                override fun onAnimationStart(animation: Animation?) {}
            })
        }

        return animation
    }
}