package at.sunilson.liveticker.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.core.content.res.ResourcesCompat
import at.sunilson.liveticker.presentation.R
import com.google.android.material.textfield.TextInputLayout

class CustomFontTextInputLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
) : TextInputLayout(context, attributeSet, style) {
    init {
        typeface = ResourcesCompat.getFont(context, R.font.font)
    }
}