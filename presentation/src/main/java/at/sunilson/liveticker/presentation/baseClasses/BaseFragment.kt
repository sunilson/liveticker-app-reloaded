package at.sunilson.liveticker.presentation.baseClasses

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.core.utils.Do
import at.sunilson.liveticker.presentation.R
import at.sunilson.liveticker.presentation.interfaces.BackpressInterceptor
import at.sunilson.liveticker.presentation.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<ViewModel : BaseViewModel<E>, E> : Fragment(), CoroutineScope {

    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    abstract val viewModel: ViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer { onNavigationEvent(it) })
        viewModel.back.observe(viewLifecycleOwner, Observer { findNavController().popBackStack() })
        viewModel.toasts.observe(viewLifecycleOwner, Observer { context?.showToast(it) })
    }

    abstract fun onNavigationEvent(event: E)

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
    override fun onCreateAnimation(transit: Int, enter: Boolean, nextAnim: Int): Animation? {

        //Show above previous fragment for animation purposes
        if (nextAnim == R.anim.move_in_from_right || nextAnim == R.anim.move_out_to_right) {
            ViewCompat.setTranslationZ(view!!, 200f)
        } else if (nextAnim == R.anim.move_in_from_left_slightly || nextAnim == R.anim.move_out_to_left_slightly) {
            ViewCompat.setTranslationZ(view!!, 100f)
        }

        return super.onCreateAnimation(transit, enter, nextAnim)
    }
     */

    protected fun setNavColors(
        @ColorRes statusColor: Int = android.R.color.white,
        @ColorRes navColor: Int = statusColor,
        darkStatus: Boolean = true,
        darkNav: Boolean = darkStatus,
        transparent: Boolean = false,
        onlyNavTransparent: Boolean = false
    ) {
        activity?.let { activity ->
            //Make bars opaque again
            activity.window.clearFlags(FLAG_TRANSLUCENT_NAVIGATION)
            activity.window.clearFlags(FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            //Set status and nav bar transparent and draw content below
            if (transparent) {
                activity.window.addFlags(FLAG_TRANSLUCENT_NAVIGATION)
                if (!onlyNavTransparent) {
                    activity.window.addFlags(FLAG_TRANSLUCENT_STATUS)
                } else {
                    activity.window.statusBarColor = ContextCompat.getColor(activity, statusColor)
                }
                activity.window.decorView.systemUiVisibility =
                    SYSTEM_UI_FLAG_LAYOUT_STABLE or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    activity.window.decorView.systemUiVisibility =
                        activity.window.decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }
                return
            }

            activity.window.decorView.systemUiVisibility =
                activity.window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

            //Set colors
            activity.window.navigationBarColor = ContextCompat.getColor(activity, navColor)
            activity.window.statusBarColor = ContextCompat.getColor(activity, statusColor)

            //Set light or dark icons
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                activity.window.decorView.systemUiVisibility = if (!darkNav) {
                    activity.window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                } else {
                    activity.window.decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                }

                activity.window.decorView.systemUiVisibility = if (!darkStatus) {
                    activity.window.decorView.systemUiVisibility or SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    activity.window.decorView.systemUiVisibility and SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}