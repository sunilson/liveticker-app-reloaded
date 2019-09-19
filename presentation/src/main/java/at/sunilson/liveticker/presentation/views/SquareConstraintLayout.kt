package at.sunilson.liveticker.presentation.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import at.sunilson.liveticker.presentation.R

/**
 * ConstraintLayout that adjusts it height to always be the same as its width
 */
class SquareConstraintLayout @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    style: Int = 0
) : ConstraintLayout(context, attributeSet, style) {

    private val useWidth: Boolean

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.SquareConstraintLayout,
            0, 0
        ).apply {
            try {
                useWidth = getBoolean(R.styleable.SquareConstraintLayout_useWidth, true)
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measurement = if (useWidth) widthMeasureSpec else heightMeasureSpec
        super.onMeasure(measurement, measurement)
    }
}