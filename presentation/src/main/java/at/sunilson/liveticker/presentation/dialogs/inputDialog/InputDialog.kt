package at.sunilson.liveticker.presentation.dialogs.inputDialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import at.sunilson.liveticker.presentation.R
import at.sunilson.liveticker.presentation.baseClasses.BaseDialogFragment
import at.sunilson.liveticker.presentation.databinding.InputDialogFragmentBinding
import at.sunilson.liveticker.presentation.interfaces.InputDialogListener
import kotlinx.android.synthetic.main.input_dialog_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InputDialog private constructor() : BaseDialogFragment() {

    private val viewModel: InputDialogViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            viewModel.title.value = it.getString("title")
            viewModel.hint.value = it.getString("hint")
            viewModel.confirm.value = it.getString("confirm")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = DataBindingUtil.inflate<InputDialogFragmentBinding>(
            inflater,
            R.layout.input_dialog_fragment,
            container,
            false
        )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        input.requestFocus()
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            if (it is InputDialogViewModel.ConfirmClicked && it.text?.isNotEmpty() == true) {
                (parentFragment as? InputDialogListener)?.inputHappened(it.text.toString())
                dismiss()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    companion object {
        fun newInstance(title: String, hint: String, confirm: String): InputDialog {
            return InputDialog().apply {
                arguments = Bundle().apply {
                    putString("title", title)
                    putString("hint", hint)
                    putString("confirm", confirm)
                }
            }
        }
    }
}