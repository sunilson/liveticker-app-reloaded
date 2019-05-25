package at.sunilson.liveticker

import org.koin.dsl.module

val appModule = module {
    single { Navigator() }
}
