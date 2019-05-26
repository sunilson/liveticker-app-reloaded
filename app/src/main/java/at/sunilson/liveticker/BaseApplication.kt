package at.sunilson.liveticker


import android.app.Application
import at.sunilson.liveticker.authentication.authenticationModule
import at.sunilson.liveticker.network.dataModule
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            modules(
                appModule,
                dataModule,
                authenticationModule
            )
        }
    }
}