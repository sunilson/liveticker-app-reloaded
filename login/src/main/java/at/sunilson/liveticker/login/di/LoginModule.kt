package at.sunilson.liveticker.login.di

import at.sunilson.liveticker.login.domain.LoginUsecase
import at.sunilson.liveticker.login.domain.RegisterUseCase
import at.sunilson.liveticker.login.presentation.login.LoginViewModel
import at.sunilson.liveticker.login.presentation.login.LoginViewModelImpl
import at.sunilson.liveticker.login.presentation.register.RegisterViewModel
import at.sunilson.liveticker.login.presentation.register.RegisterViewModelImpl
import org.koin.dsl.module
import org.koin.androidx.viewmodel.dsl.viewModel

val loginModule = module {
    viewModel<LoginViewModel> {
        LoginViewModelImpl(
            get()
        )
    }
    viewModel<RegisterViewModel> {
        RegisterViewModelImpl(
            get()
        )
    }
    factory { RegisterUseCase(get()) }
    factory { LoginUsecase(get()) }
}