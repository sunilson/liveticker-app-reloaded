package at.sunilson.liveticker.liveticker.di

import at.sunilson.liveticker.liveticker.data.LiveTickerRepositoryImpl
import at.sunilson.liveticker.liveticker.domain.*
import at.sunilson.liveticker.liveticker.presentation.comments.CommentsRecyclerAdapter
import at.sunilson.liveticker.liveticker.presentation.liveticker.LivetickerRecyclerAdapter
import at.sunilson.liveticker.liveticker.presentation.liveticker.LivetickerViewModel
import at.sunilson.liveticker.liveticker.presentation.liveticker.LivetickerViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val livetickerModule = module {
    viewModel<LivetickerViewModel> {
        LivetickerViewModelImpl(
            get(),
            get(),
            get(),
            get(),
            get(),
            get()
        )
    }
    single<LivetickerRepository> { LiveTickerRepositoryImpl(get()) }
    factory { LivetickerRecyclerAdapter() }
    factory { CommentsRecyclerAdapter() }
    factory { GetLivetickerUseCase(get()) }
    factory { SetNotificationUseCase(get(), get()) }
    factory { AddCommentUseCase(get(), get()) }
    factory { GetCommentsUseCase(get()) }
    factory { CheerUseCase(get()) }
}