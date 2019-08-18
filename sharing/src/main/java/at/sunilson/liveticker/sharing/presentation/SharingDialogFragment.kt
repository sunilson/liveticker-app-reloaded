package at.sunilson.liveticker.sharing.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintSet
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.ChangeBounds
import androidx.transition.Fade
import androidx.transition.TransitionManager
import androidx.transition.TransitionSet
import at.sunilson.liveticker.core.systemutils.ClipboardManager
import at.sunilson.liveticker.presentation.*
import at.sunilson.liveticker.presentation.baseClasses.BaseFullscreenDialogFragment
import at.sunilson.liveticker.sharing.R
import kotlinx.android.synthetic.main.fragment_sharing.*
import org.koin.android.ext.android.inject
import kotlin.math.max

class SharingDialogFragment : BaseFullscreenDialogFragment() {

    private val clipboardManager: ClipboardManager by inject()
    private val args: SharingDialogFragmentArgs by navArgs()
    private val maxRadius
        get() = max(fragment_sharing_container.measuredWidth, fragment_sharing_container.measuredHeight).toFloat()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_sharing, container, false)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Intercept back button
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                hideToView(fragment_sharing_container) { findNavController().popBackStack() }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        reveal()

        fragment_sharing_1.setOnClickListener {
            hideToView(it) {
                clipboardManager.addTextToClipboard(
                    "https://www.my-ticker.live/liveticker/edit/${args.editUrl}",
                    "Liveticker Edit-URL"
                )
                context?.showToast(R.string.copied_edit_url)
                findNavController().popBackStack()
            }
        }


        fragment_sharing_2.setOnClickListener {
            hideToView(it) {
                clipboardManager.addTextToClipboard(
                    "https://www.my-ticker.live/liveticker/${args.viewUrl}",
                    "Liveticker Sharing-URL"
                )
                context?.showToast(R.string.copied_liveticker_url)
                findNavController().popBackStack()
            }
        }
    }

    private fun reveal() {
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
                val constraintSetNew = ConstraintSet().apply {
                    clone(
                        context,
                        if (args.editUrl != null) R.layout.fragment_sharing_finished else R.layout.fragment_sharing_finished_view_only
                    )
                }
                val transitionSet = TransitionSet().apply {
                    addTransition(ChangeBounds())
                    addTransition(Fade())
                }

                TransitionManager.beginDelayedTransition(fragment_sharing_container, transitionSet)
                constraintSetNew.applyTo(fragment_sharing_container)
            }
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