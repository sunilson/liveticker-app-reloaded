package at.sunilson.liveticker

import androidx.work.WorkManager
import at.sunilson.liveticker.home.HomeNavigation
import at.sunilson.liveticker.liveticker.LivetickerNavigation
import at.sunilson.liveticker.login.LoginNavigation
import at.sunilson.liveticker.presentation.MainViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { Navigator() } bind HomeNavigation::class bind LoginNavigation::class bind LivetickerNavigation::class
    single(named("fileProviderName")) { "at.sunilson.liveticker.fileprovider" }
    single { WorkManager.getInstance(androidApplication()) }
    viewModel<MainViewModel> {
        MainViewModelImpl(get())
    }
}
