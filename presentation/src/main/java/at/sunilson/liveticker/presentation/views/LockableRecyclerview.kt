package at.sunilson.liveticker.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class LockableRecyclerview(context: Context, attrs: AttributeSet) :
    RecyclerView(context, attrs) {

    var locked = false

    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
        var handled = false
        if (!locked) {
            handled = super.onInterceptTouchEvent(e)
        }
        return handled
    }

    override fun onTouchEvent(e: MotionEvent?): Boolean {
        var handled = false

        if (!locked) {
            handled = super.onTouchEvent(e)
        }

        return handled
    }

    override fun onStartNestedScroll(child: View?, target: View?, nestedScrollAxes: Int): Boolean {
        var handled = false
        if (!locked) handled = super.onStartNestedScroll(child, target, nestedScrollAxes)
        return handled
    }

    override fun onNestedPreScroll(target: View?, dx: Int, dy: Int, consumed: IntArray?) {
        if (!locked) super.onNestedPreScroll(target, dx, dy, consumed)

    }

    override fun onStopNestedScroll(child: View?) {
        if (!locked) super.onStopNestedScroll(child)
    }

    override fun onNestedPreFling(target: View?, velocityX: Float, velocityY: Float): Boolean {
        var handled = false
        if (!locked) handled = super.onNestedPreFling(target, velocityX, velocityY)
        return handled
    }
}