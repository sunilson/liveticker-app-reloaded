package at.sunilson.liveticker.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.login.R
import at.sunilson.liveticker.login.databinding.FragmentRegisterBinding
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment() {

    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentRegisterBinding>(inflater, R.layout.fragment_register, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                RegisterViewModel.Login -> findNavController().popBackStack()
            }
        })
    }
}