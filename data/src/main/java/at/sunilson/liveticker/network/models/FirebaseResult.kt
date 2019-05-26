package at.sunilson.liveticker.network.models

/**
 * Represents a result from a firebase query. Either the result or the error should be null
 */
typealias FirebaseResult<T> = Pair<T?, Throwable?>