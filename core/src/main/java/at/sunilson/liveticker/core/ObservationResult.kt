package at.sunilson.liveticker.core

sealed class ObservationResult<T>(val data: T) {
    class Added<T>(data: T): ObservationResult<T>(data)
    class Deleted<T>(data: T): ObservationResult<T>(data)
    class Modified<T>(data: T): ObservationResult<T>(data)
}

fun <T> ObservationResult<T>.copy(data: T): ObservationResult<T> {
    return when (this) {
        is ObservationResult.Added -> ObservationResult.Added(
            data
        )
        is ObservationResult.Deleted -> ObservationResult.Deleted(
            data
        )
        is ObservationResult.Modified -> ObservationResult.Modified(
            data
        )
    }
}