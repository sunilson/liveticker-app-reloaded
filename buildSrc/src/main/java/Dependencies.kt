const val room_version = "2.1.0-beta01"
const val nav_version = "2.1.0-alpha05"
const val koin_version = "2.0.0"

object AndroidX {
    val constraintLayout = "androidx.constraintlayout:constraintlayout:2.0.0-beta1"
    val appcompat = "androidx.appcompat:appcompat:1.1.0-beta01"
    val androidx_core = "androidx.core:core-ktx:1.1.0-beta01"

    val lifecycle_scope = "androidx.lifecycle:lifecycle-runtime-ktx:2.2.0-alpha01"
    val lifecycle_extensions = "androidx.lifecycle:lifecycle-extensions:2.0.0"

    val room = "androidx.room:room-runtime:$room_version"
    val roomAnnotations = "androidx.room:room-compiler:$room_version"
    val room_kotlin = "androidx.room:room-ktx:$room_version"

    val navigation_fragment = "androidx.navigation:navigation-fragment-ktx:$nav_version"
    val navigation_ui = "androidx.navigation:navigation-ui-ktx:$nav_version"
    val navigation_safe_args = "androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version"
}

object Kotlin {
    val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-core:1.2.1"
}

object UILibs {
    val viewPager_pagination = "com.tbuonomo.andrui:viewpagerdotsindicator:3.0.3"
    val recyclerView_animations = "jp.wasabeef:recyclerview-animators:3.0.0"
}

object DILibs {
    val koin = "org.koin:koin-android:$koin_version"
    val koinLifecycle = "org.koin:koin-androidx-scope:$koin_version"
    val koinViewModel = "org.koin:koin-androidx-viewmodel:$koin_version"
}

object ThirdPartyLibs {
    val timber = "com.jakewharton.timber:timber:4.7.1"
    val joda = "joda-time:joda-time:2.10.2"
    val result = "com.github.kittinunf.result:result:2.2.0"
    val resultCoroutines = "com.github.kittinunf.result:result-coroutines:2.2.0"
    val circularImageView = "de.hdodenhof:circleimageview:3.0.0"
    val glide = "com.github.bumptech.glide:glide:4.9.0"
}

object TestLibs {
    val junit = "junit:junit:4.12"
    val androidx_runner = "androidx.test:runner:1.1.1"
    val espresso = "androidx.test.espresso:espresso-core:3.1.1"
    val room = "androidx.room:room-testing:$room_version"
    val mockk = "io.mockk:mockk:1.9.3"
    val koin = "org.koin:koin-test:2.0.0-GA6"
}

object GoogleLibs {
    val auth = "com.google.android.gms:play-services-auth:16.0.1"
    val material_components = "com.google.android.material:material:1.1.0-alpha07"
    val maps = "com.google.android.gms:play-services-maps:16.1.0"
    val places = "com.google.android.libraries.places:places:1.1.0"

}

object FirebaseLibs {
    val core = "com.google.firebase:firebase-core:16.0.9"
    val fireStore = "com.google.firebase:firebase-firestore:19.0.0"
    val authentication = "com.google.firebase:firebase-auth:17.0.0"
}