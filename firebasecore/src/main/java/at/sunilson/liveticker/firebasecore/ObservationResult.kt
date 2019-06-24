package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.core.models.ModelWithId

sealed class ChangeType

sealed class ObservationResult<T: ModelWithId>(val data: T) {
    class Added<T: ModelWithId>(data: T): ObservationResult<T>(data)
    class Deleted<T: ModelWithId>(data: T): ObservationResult<T>(data)
    class Modified<T: ModelWithId>(data: T): ObservationResult<T>(data)
}