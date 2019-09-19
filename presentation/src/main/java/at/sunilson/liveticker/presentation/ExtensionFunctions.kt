package at.sunilson.liveticker.presentation

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.ObservableList
import androidx.fragment.app.Fragment
import at.sunilson.liveticker.core.ObservationResult
import at.sunilson.liveticker.core.models.ModelWithId
import android.app.Activity
import android.content.Intent
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.marginBottom
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.core.view.marginTop


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

fun Context.hasPermissions(vararg permissions: String): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        permissions.forEach {
            if (ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
    }
    return true
}

fun Fragment.hideKeyboard() {
    val imm =
        requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(requireView().rootView.windowToken, 0)
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

fun <T : ModelWithId> ObservableList<T>.handleObservationResults(changes: List<ObservationResult<T>>) {
    val deletions = changes.filter { it is ObservationResult.Deleted }.map { it.data }
    val additions = changes.filter { it is ObservationResult.Added }.map { it.data }
    val modifications = changes.filter { it is ObservationResult.Modified }.map { it.data }

    addAll(additions)
    deletions.forEach { remove(it) }
    modifications.forEach { updateWithId(it) }
}

fun <T : ModelWithId> ObservableList<T>.updateWithId(value: T) {
    val index = indexOfFirst { it.id == value.id }
    set(index, value)
}

fun <T : ModelWithId> ObservableList<T>.removeWithId(value: T) {
    val index = indexOfFirst { it.id == value.id }
    removeAt(index)
}

fun Context.showToast(@StringRes message: Int, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}

fun Context.showToast(message: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, message, length).show()
}

fun Intent.canBeHandled(context: Context) = this.resolveActivity(context.packageManager) != null

fun Fragment.safeStartActivity(intent: Intent) {
    if (intent.canBeHandled(activity ?: return)) {
        startActivity(intent)
    }
}

fun Fragment.safeStartActivityForResult(intent: Intent, requestCode: Int) {
    if (intent.canBeHandled(activity ?: return)) {
        startActivityForResult(intent, requestCode)
    }
}

fun View.setMargins(
    left: Int = this.marginLeft,
    right: Int = this.marginRight,
    top: Int = this.marginTop,
    bottom: Int = this.marginBottom
) {
    val layoutParams = this.layoutParams
    if (layoutParams is CoordinatorLayout.LayoutParams) {
        this.layoutParams = layoutParams.apply {
            setMargins(
                left,
                right,
                top,
                bottom
            )
        }
    } else if (layoutParams is ConstraintLayout.LayoutParams) {
        this.layoutParams = layoutParams.apply {
            setMargins(
                left,
                right,
                top,
                bottom
            )
        }
    }
}