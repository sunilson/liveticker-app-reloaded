package at.sunilson.liveticker.liveticker.di

import at.sunilson.liveticker.liveticker.data.LiveTickerRepositoryImpl
import at.sunilson.liveticker.liveticker.domain.*
import at.sunilson.liveticker.liveticker.presentation.liveticker.LivetickerViewModel
import at.sunilson.liveticker.liveticker.presentation.liveticker.LivetickerViewModelImpl
import at.sunilson.liveticker.liveticker.presentation.photo.PhotoViewModel
import at.sunilson.liveticker.liveticker.presentation.photo.PhotoViewModelImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val livetickerModule = module {
    viewModel<PhotoViewModel> { PhotoViewModelImpl() }
    viewModel<LivetickerViewModel> {
        LivetickerViewModelImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<LivetickerRepository> {
        LiveTickerRepositoryImpl(
            get(),
            androidApplication().contentResolver
        )
    }
    factory { CreateImageFileUseCase(androidContext(), get(named("fileProviderName"))) }
    factory { GetLocalImagesUseCase(get()) }
    factory { GetLivetickerUseCase(get(), get()) }
    factory { SetNotificationUseCase(get(), get()) }
    factory { AddCommentUseCase(get(), get()) }
    factory { GetCommentsUseCase(get()) }
    factory { CheerUseCase(get()) }
}