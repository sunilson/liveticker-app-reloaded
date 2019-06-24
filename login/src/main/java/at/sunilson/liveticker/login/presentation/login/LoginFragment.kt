package at.sunilson.liveticker.login.presentation.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import at.sunilson.liveticker.login.LoginNavigation
import at.sunilson.liveticker.login.R
import at.sunilson.liveticker.login.databinding.FragmentLoginBinding
import at.sunilson.liveticker.presentation.baseClasses.BaseFragment
import at.sunilson.liveticker.presentation.baseClasses.NavigationEvent
import at.sunilson.liveticker.presentation.enterChildViewsFromBottomDelayed
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : BaseFragment<LoginViewModel>() {

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

    override fun onNavigationEvent(event: NavigationEvent) {
        when (event) {
            LoginViewModel.Register -> findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
            LoginViewModel.LoggedIn -> loginNavigation.moveToHome()
        }
    }
}