package at.sunilson.liveticker.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import at.sunilson.liveticker.core.REQUEST_PERMISSIONS

//Converts dp to px
fun Int.convertToPx(context: Context): Int {
    return (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

//Converts px to dp
fun Int.convertToDp(context: Context): Int {
    return (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
}

//Converts dp to px
fun Float.convertToPx(context: Context): Float {
    return (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
}

//Converts px to dp
fun Float.convertToDp(context: Context): Float {
    return (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT))
}

/**
 * Do something after the first measure is done where views height & width is greater than 0
 */
inline fun View.doAfterMeasure(crossinline cb: View.() -> Unit) {
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            cb()
        }
    })
}

fun Context.hasPermission(permission: String): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    } else {
        true
    }
}

/**
 * Displays an alert to the user
 *
 * @param title
 * @param message
 * @param positiveButton Text of the confirmation button
 * @param onAction Callback that emits true if dialog was cancelled and false if confirmed
 * @return True if confirmed
 */
fun Context.showConfirmationDialog(
    title: String,
    message: String,
    positiveButton: String,
    onAction: (Boolean) -> Unit = {}
): AlertDialog {
    val dialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveButton) { _, _ -> onAction(true) }
        .setNegativeButton(R.string.cance) { _, _ -> onAction(false) }.show()
    val button1 = dialog.findViewById<Button>(android.R.id.button1)
    val button2 = dialog.findViewById<Button>(android.R.id.button2)
    button1?.setTextColor(ContextCompat.getColor(this, R.color.baseTextColor))
    button2?.setTextColor(ContextCompat.getColor(this, R.color.baseTextColor))
    return dialog
}

val View.centerX
    get() = x + width / 2

val View.centerY
    get() = y + height / 2