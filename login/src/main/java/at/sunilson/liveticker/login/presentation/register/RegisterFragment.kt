package at.sunilson.liveticker.login.presentation.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.login.LoginNavigation
import at.sunilson.liveticker.login.R
import at.sunilson.liveticker.login.databinding.FragmentRegisterBinding
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import at.sunilson.liveticker.presentation.enterChildViewsFromBottomDelayed
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_register.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : BaseFragment<RegisterViewModel>() {

    override val viewModel: RegisterViewModel by viewModel()
    private val loginNavigation: LoginNavigation by inject()
    private var animated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentRegisterBinding>(inflater, R.layout.fragment_register, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(!animated) {
            fragment_register_content.enterChildViewsFromBottomDelayed(initialDelay = 300)
            animated = true
        }
    }

    override fun onNavigationEvent(event: NavigationEvent) {
        when (event) {
            RegisterViewModel.Login -> findNavController().popBackStack()
            RegisterViewModel.Registered -> loginNavigation.moveToHome()
        }
    }
}