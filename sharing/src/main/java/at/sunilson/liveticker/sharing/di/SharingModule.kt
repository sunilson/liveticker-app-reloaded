package at.sunilson.liveticker.sharing.di

import at.sunilson.liveticker.sharing.data.SharingRepositoryImpl
import at.sunilson.liveticker.sharing.domain.GetEditUrlUseCase
import at.sunilson.liveticker.sharing.domain.SharingRepository
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val sharingModule = module {
    single<SharingRepository> { SharingRepositoryImpl(get()) }
    factory { GetEditUrlUseCase(get()) }
}