package at.sunilson.liveticker.core.utils

object Do {
    inline infix fun <reified T> exhaustive(any: T?) = any
}