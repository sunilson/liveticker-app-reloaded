package at.sunilson.liveticker.login.domain

sealed class ValidationException: Exception()
class EmailInvalid: ValidationException()
class PasswordInvalid: ValidationException()