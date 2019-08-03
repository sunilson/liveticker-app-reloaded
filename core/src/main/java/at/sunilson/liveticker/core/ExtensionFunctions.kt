package at.sunilson.liveticker.core

fun String.isValidEmail() = android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
