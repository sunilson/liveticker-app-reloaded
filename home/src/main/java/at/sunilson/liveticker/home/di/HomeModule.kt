package at.sunilson.liveticker.home.di

import at.sunilson.liveticker.home.data.HomeRepository
import at.sunilson.liveticker.home.data.HomeRepositoryImpl
import at.sunilson.liveticker.home.presentation.home.HomeFragment
import at.sunilson.liveticker.home.presentation.home.HomeViewModel
import at.sunilson.liveticker.home.presentation.home.HomeViewModelImpl
import at.sunilson.liveticker.home.presentation.home.LivetickerRecyclerAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val homeModule = module {
    viewModel<HomeViewModel> { HomeViewModelImpl(get(), get()) }
    single<HomeRepository> { HomeRepositoryImpl(get()) }
    factory { LivetickerRecyclerAdapter() }
}