package at.sunilson.liveticker.authentication

import com.google.firebase.auth.FirebaseAuth
import org.koin.dsl.module

val authenticationModule = module {
    single<IAuthenticationRepository> { FirebaseAuthenticationRepository(FirebaseAuth.getInstance()) }
}