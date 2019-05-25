package at.sunilson.liveticker.network

import com.google.firebase.firestore.FirebaseFirestore
import org.koin.dsl.module

val networkModule = module {
    single<IRemoteRepository> { FirebaseRepository(FirebaseFirestore.getInstance()) }
}