package at.sunilson.liveticker.presentation.views

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import at.sunilson.liveticker.presentation.R


class DiagonalCutView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) :
    ConstraintLayout(context, attributeSet) {

    private val paint = Paint(ANTI_ALIAS_FLAG)
    private val pdMode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    private val path = Path()

    private var offset: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    private var direction: Int = 0
        set(value) {
            field = value
            invalidate()
        }


    init {
        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.DiagonalCutView,
            0, 0
        ).apply {
            try {
                offset = getFloat(R.styleable.DiagonalCutView_offset, 100f)
                direction = getInt(R.styleable.DiagonalCutView_direction, 0)
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

        when (direction) {
            0 -> {
                path.moveTo(0f, height.toFloat())
                path.lineTo(width.toFloat(), height.toFloat())
                path.lineTo(width.toFloat(), height.toFloat() - offset)
            }
            1 -> {
                path.moveTo(0f, 0f)
                path.lineTo(width.toFloat(), offset)
                path.lineTo(width.toFloat(), 0f)
            }
            2 -> {
                path.moveTo(0f, height.toFloat())
                path.lineTo(offset, 0f)
                path.lineTo(0f, 0f)
            }
            3 -> {
                path.moveTo(width.toFloat() - offset, height.toFloat())
                path.lineTo(width.toFloat(), 0f)
                path.lineTo(width.toFloat() - offset, 0f)
            }
        }


        path.close()
        canvas?.drawPath(path, paint)

        saveCount?.let { canvas.restoreToCount(it) }
        paint.xfermode = null
    }
}