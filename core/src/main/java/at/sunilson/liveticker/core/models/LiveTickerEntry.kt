package at.sunilson.liveticker.core.models

sealed class LiveTickerEntry : ModelWithId {
    abstract val time: Long

    data class TextLiveTickerEntry(
        override var id: String,
        override val time: Long,
        val reaction: String,
        val text: String
    ) : LiveTickerEntry()

    data class ImageLivetickerEntry(
        override var id: String,
        override val time: Long,
        val caption: String,
        val image: String?
    ) : LiveTickerEntry()
}