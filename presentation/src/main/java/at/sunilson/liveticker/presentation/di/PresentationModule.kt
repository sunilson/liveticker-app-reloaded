package at.sunilson.liveticker.presentation.di

import at.sunilson.liveticker.presentation.dialogs.inputDialog.InputDialogViewModel
import at.sunilson.liveticker.presentation.dialogs.inputDialog.InputDialogViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val presentationModule = module {
    viewModel<InputDialogViewModel> { InputDialogViewModelImpl() }
}