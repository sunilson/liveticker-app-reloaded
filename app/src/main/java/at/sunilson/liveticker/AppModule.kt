package at.sunilson.liveticker

import at.sunilson.liveticker.home.HomeNavigation
import at.sunilson.liveticker.liveticker.LivetickerNavigation
import at.sunilson.liveticker.login.LoginNavigation
import at.sunilson.liveticker.presentation.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { Navigator() } bind HomeNavigation::class bind LoginNavigation::class bind LivetickerNavigation::class
    viewModel<MainViewModel> {
        MainViewModelImpl(get())
    }
}
