package at.sunilson.liveticker.home.presentation

import at.sunilson.liveticker.home.presentation.home.HomeViewModel
import at.sunilson.liveticker.home.presentation.home.HomeViewModelImpl
import at.sunilson.liveticker.home.presentation.livetickerCreation.LivetickerCreationViewModel
import at.sunilson.liveticker.home.presentation.livetickerCreation.LivetickerCreationViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel<HomeViewModel> { HomeViewModelImpl(get(), get()) }
    viewModel<LivetickerCreationViewModel> { LivetickerCreationViewModelImpl(get(), get()) }
}