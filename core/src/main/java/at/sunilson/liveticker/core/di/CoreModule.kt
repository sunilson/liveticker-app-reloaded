package at.sunilson.liveticker.core.di

import at.sunilson.liveticker.core.systemutils.ClipboardManager
import at.sunilson.liveticker.core.systemutils.ClipboardManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val coreModule = module {
    single<ClipboardManager> { ClipboardManagerImpl(androidContext()) }
}