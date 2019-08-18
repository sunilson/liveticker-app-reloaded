package at.sunilson.liveticker.login.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.core.utils.Do
import at.sunilson.liveticker.login.LoginNavigation
import at.sunilson.liveticker.login.R
import at.sunilson.liveticker.login.databinding.FragmentLoginBinding
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.enterChildViewsFromBottomDelayed
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<LoginViewModel, LoginNavigationEvent>() {

    override val viewModel: LoginViewModel by viewModel()
    private val loginNavigation: LoginNavigation by inject()
    private var animated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = generateBinding<FragmentLoginBinding>(inflater, R.layout.fragment_login, container)
        binding.viewModel = viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!animated) {
            fragment_login_content.enterChildViewsFromBottomDelayed(initialDelay = 300)
            animated = true
        }
    }

    override fun onNavigationEvent(event: LoginNavigationEvent) {
        Do exhaustive when (event) {
            LoginNavigationEvent.Register -> findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            LoginNavigationEvent.LoggedIn -> loginNavigation.moveToHome()
        }
    }
}