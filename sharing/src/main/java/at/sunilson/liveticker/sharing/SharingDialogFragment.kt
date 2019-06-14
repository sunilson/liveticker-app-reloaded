package at.sunilson.liveticker.sharing

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.baseClasses.BaseFullscreenDialogFragment
import at.sunilson.liveticker.presentation.centerX
import at.sunilson.liveticker.presentation.centerY
import at.sunilson.liveticker.presentation.circularReveal
import at.sunilson.liveticker.presentation.doAfterMeasure
import kotlinx.android.synthetic.main.fragment_sharing.*

class SharingDialogFragment : BaseFullscreenDialogFragment() {

    private val maxRadius
        get() = Math.max(fragment_sharing_container.measuredWidth, fragment_sharing_container.measuredHeight).toFloat()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sharing, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Intercept back button
        return object : Dialog(context, theme) {
            override fun onBackPressed() {
                hideToView(fragment_sharing_container) { findNavController().popBackStack() }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Reveal sharing fragment
        fragment_sharing_container.doAfterMeasure {
            circularReveal(
                fragment_sharing_container.centerX.toInt(),
                fragment_sharing_container.centerY.toInt(),
                0f,
                maxRadius,
                300L
            ) {
                //Transition in sharing icons
                val constraintSetNew = ConstraintSet().apply { clone(context, R.layout.fragment_sharing_finished) }
                val transitionSet = TransitionSet().apply {
                    addTransition(ChangeBounds())
                    addTransition(Fade())
                }
                TransitionManager.beginDelayedTransition(fragment_sharing_container, transitionSet)
                constraintSetNew.applyTo(fragment_sharing_container)
            }
        }

        fragment_sharing_1.setOnClickListener {
            hideToView(it) { findNavController().popBackStack() }
        }


        fragment_sharing_2.setOnClickListener {
            hideToView(it) { findNavController().popBackStack() }
        }
    }

    private fun hideToView(view: View, action: () -> Unit) {
        fragment_sharing_container.circularReveal(
            view.centerX.toInt(),
            view.centerY.toInt(),
            maxRadius,
            0f,
            300L
        ) { action() }
    }
}