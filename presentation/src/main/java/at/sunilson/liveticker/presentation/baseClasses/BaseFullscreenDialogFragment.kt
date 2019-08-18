package at.sunilson.liveticker.presentation.baseClasses

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment

abstract class BaseFullscreenDialogFragment : DialogFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Remove dimming
        setStyle(STYLE_NO_FRAME, theme)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Intercept back button
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                backPressed()
            }
        }
    }

    open fun backPressed() {
        dismiss()
    }

    override fun onResume() {
        super.onResume()

        //Full width dialog
        val params = dialog?.window?.attributes
        params?.width = ViewGroup.LayoutParams.MATCH_PARENT
        params?.height = ViewGroup.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = params as android.view.WindowManager.LayoutParams
    }
}