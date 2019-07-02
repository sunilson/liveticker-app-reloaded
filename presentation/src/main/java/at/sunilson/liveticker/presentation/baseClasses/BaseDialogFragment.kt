package at.sunilson.liveticker.presentation.baseClasses

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import at.sunilson.liveticker.presentation.R


abstract class BaseDialogFragment : DialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.attributes?.windowAnimations = R.style.dialogAnimation
    }
}