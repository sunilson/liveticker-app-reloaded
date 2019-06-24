package at.sunilson.liveticker


import android.app.Application
import at.sunilson.liveticker.authentication.authenticationModule
import at.sunilson.liveticker.firebasecore.firebaseModule
import at.sunilson.liveticker.home.di.homeModule
import at.sunilson.liveticker.liveticker.di.livetickerModule
import at.sunilson.liveticker.livetickercreation.di.livetickerCreationModule
import at.sunilson.liveticker.location.locationModule
import at.sunilson.liveticker.login.di.loginModule
import com.google.firebase.FirebaseApp
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@BaseApplication)
            modules(
                appModule,
                authenticationModule,
                loginModule,
                homeModule,
                locationModule,
                firebaseModule,
                livetickerModule,
                livetickerCreationModule
            )
        }
    }
}