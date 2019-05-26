package at.sunilson.liveticker.login.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.login.R
import at.sunilson.liveticker.login.databinding.FragmentLoginBinding
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment() {

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentLoginBinding>(inflater, R.layout.fragment_login, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.navigationEvents.observe(viewLifecycleOwner, Observer {
            when (it) {
                LoginViewModel.Register -> findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            }
        })
    }
}