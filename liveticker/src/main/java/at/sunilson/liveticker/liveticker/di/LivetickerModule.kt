package at.sunilson.liveticker.liveticker.di

import at.sunilson.liveticker.liveticker.data.LiveTickerRepositoryImpl
import at.sunilson.liveticker.liveticker.data.LivetickerRepository
import at.sunilson.liveticker.liveticker.presentation.LivetickerRecyclerAdapter
import at.sunilson.liveticker.liveticker.presentation.LivetickerViewModel
import at.sunilson.liveticker.liveticker.presentation.LivetickerViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val livetickerModule = module {
    viewModel<LivetickerViewModel> { LivetickerViewModelImpl(get()) }
    single<LivetickerRepository> { LiveTickerRepositoryImpl(get()) }
    factory { LivetickerRecyclerAdapter() }
}