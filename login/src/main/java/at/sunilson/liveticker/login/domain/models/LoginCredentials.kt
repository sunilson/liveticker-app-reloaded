package at.sunilson.liveticker.login.domain.models

data class LoginCredentials(val email: String, val password: String, val userName: String = "")
