package at.sunilson.liveticker.core.systemutils

import android.content.ClipData
import android.content.Context

interface ClipboardManager {
    fun addTextToClipboard(text: String, label: String = "Inserted caption")
}

internal class ClipboardManagerImpl(private val context: Context) : ClipboardManager {

    private val systemClipboard: android.content.ClipboardManager
        get() = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager

    override fun addTextToClipboard(text: String, label: String) {
        val data = ClipData.newPlainText(label, text)
        systemClipboard.setPrimaryClip(data)
    }
}