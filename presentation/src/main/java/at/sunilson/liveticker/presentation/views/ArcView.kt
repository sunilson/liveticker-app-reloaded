package at.sunilson.liveticker.presentation.views

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.view.View
import at.sunilson.liveticker.presentation.R
import at.sunilson.liveticker.presentation.convertToPx


class ArcView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, s: Int = 0) :
    View(context, attributeSet, s) {

    private val paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.RED
    }
    private val pdMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private val path = Path()

    var arcHeight: Int = 0
        set(value) {
            field = value
            invalidate()
        }

    var arcHeightDimension: Int = 0
        set(value) {
            field = value
            arcHeight = value.convertToPx(context)
        }

    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.ArcView,
            0, 0
        ).apply {
            try {
                arcHeight = getDimensionPixelSize(R.styleable.ArcView_arcHeight, 50)
            } finally {
                recycle()
            }
        }
    }

    override fun draw(canvas: Canvas?) {
        val saveCount = canvas?.saveLayer(0f, 0f, width.toFloat(), height.toFloat(), null)
        super.draw(canvas)

        paint.xfermode = pdMode
        path.reset()
        path.moveTo(0f, height.toFloat() - arcHeight)
        path.quadTo(width.toFloat() / 2, height.toFloat() + arcHeight, width.toFloat(), height.toFloat() - arcHeight)
        path.lineTo(width.toFloat(), height.toFloat())
        path.lineTo(0f, height.toFloat())
        path.close()
        canvas?.drawPath(path, paint)

        saveCount?.let { canvas.restoreToCount(it) }
        paint.xfermode = null
    }
}