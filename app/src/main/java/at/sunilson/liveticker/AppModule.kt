package at.sunilson.liveticker

import at.sunilson.liveticker.home.HomeNavigation
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    single { Navigator() } bind HomeNavigation::class
}
