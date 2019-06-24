package at.sunilson.liveticker.livetickercreation.di

import at.sunilson.liveticker.livetickercreation.data.LivetickerCreationRepository
import at.sunilson.liveticker.livetickercreation.data.LivetickerCreationRepositoryImpl
import at.sunilson.liveticker.livetickercreation.domain.CreateLivetickerUseCase
import at.sunilson.liveticker.livetickercreation.presentation.livetickerCreation.LivetickerCreationViewModel
import at.sunilson.liveticker.livetickercreation.presentation.livetickerCreation.LivetickerCreationViewModelImpl
import at.sunilson.liveticker.livetickercreation.presentation.locationPickerDialog.LocationPickerDialogViewModel
import at.sunilson.liveticker.livetickercreation.presentation.locationPickerDialog.LocationPickerDialogViewModelImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val livetickerCreationModule = module {
    viewModel<LocationPickerDialogViewModel> { LocationPickerDialogViewModelImpl(get(), get()) }
    viewModel<LivetickerCreationViewModel> { LivetickerCreationViewModelImpl(get()) }
    single<LivetickerCreationRepository> { LivetickerCreationRepositoryImpl(get()) }
    factory { CreateLivetickerUseCase(get(), get()) }
}