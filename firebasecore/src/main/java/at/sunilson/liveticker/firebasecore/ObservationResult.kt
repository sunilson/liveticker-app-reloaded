package at.sunilson.liveticker.firebasecore

import at.sunilson.liveticker.firebasecore.models.FirebaseEntity

sealed class ObservationResult<T: FirebaseEntity>(val data: T) {
    class Added<T: FirebaseEntity>(data: T): ObservationResult<T>(data)
    class Deleted<T: FirebaseEntity>(data: T): ObservationResult<T>(data)
    class Modified<T: FirebaseEntity>(data: T): ObservationResult<T>(data)
}