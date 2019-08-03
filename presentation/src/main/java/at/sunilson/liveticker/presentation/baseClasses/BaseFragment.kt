package at.sunilson.liveticker.presentation.baseClasses

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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
import at.sunilson.liveticker.presentation.R
import at.sunilson.liveticker.presentation.interfaces.BackpressInterceptor
import at.sunilson.liveticker.presentation.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment<ViewModel : BaseViewModel> : Fragment(), CoroutineScope {

    private val job: Job = SupervisorJob()
    override val coroutineContext: CoroutineContext = Dispatchers.Main + job
    abstract val viewModel: ViewModel
    private var originalStatusBarColor: Int? = null
    private var dark: Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            if (it is Back) findNavController().popBackStack()
            else onNavigationEvent(it)
        })

        viewModel.toasts.observe(viewLifecycleOwner, Observer { context?.showToast(it) })
    }

    open fun onNavigationEvent(event: NavigationEvent) {}

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
        if (nextAnim == R.anim.move_in_from_right || nextAnim == R.anim.move_out_to_right) {
            ViewCompat.setTranslationZ(view!!, 200f)
        } else if (nextAnim == R.anim.move_in_from_left_slightly || nextAnim == R.anim.move_out_to_left_slightly) {
            ViewCompat.setTranslationZ(view!!, 100f)
        }

        return super.onCreateAnimation(transit, enter, nextAnim)
    }

    protected fun setStatusBarColor(@ColorRes color: Int? = null, dark: Boolean = true) {
        activity?.let { activity ->
            if (originalStatusBarColor == null) originalStatusBarColor = activity.window.statusBarColor

            activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

            if (color != null) {
                activity.window.statusBarColor = ContextCompat.getColor(activity, color)
            } else if (originalStatusBarColor != null) {
                originalStatusBarColor?.let { activity.window.statusBarColor = it }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!dark) activity.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                else {
                    var flags = activity.window.decorView.systemUiVisibility
                    flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    activity.window.decorView.systemUiVisibility = flags
                }
            }
        }
    }

    protected fun resetStatusBarColor() {
        setStatusBarColor()
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}