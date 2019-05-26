package at.sunilson.liveticker.login

import at.sunilson.liveticker.login.presentation.LoginViewModel
import at.sunilson.liveticker.login.presentation.LoginViewModelImpl
import at.sunilson.liveticker.login.presentation.RegisterViewModel
import at.sunilson.liveticker.login.presentation.RegisterViewModelImpl
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val loginModule = module {
    viewModel<LoginViewModel> { LoginViewModelImpl(get()) }
    viewModel<RegisterViewModel> { RegisterViewModelImpl(get()) }
}