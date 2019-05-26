package at.sunilson.liveticker


import android.app.Application
import at.sunilson.liveticker.authentication.authenticationModule
import at.sunilson.liveticker.home.presentation.homeModule
import at.sunilson.liveticker.login.loginModule
import at.sunilson.liveticker.network.dataModule
import com.google.firebase.FirebaseApp
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        Timber.plant(Timber.DebugTree())

        startKoin {
            modules(
                appModule,
                dataModule,
                authenticationModule,
                loginModule,
                homeModule
            )
        }
    }
}