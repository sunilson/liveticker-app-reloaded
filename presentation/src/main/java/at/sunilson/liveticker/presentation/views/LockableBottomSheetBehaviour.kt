package at.sunilson.liveticker.presentation.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior


class LockableBottomSheetBehavior<V : View>(context: Context, attrs: AttributeSet) :
    BottomSheetBehavior<V>(context, attrs) {

    var locked = false

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        var handled = false
        if (!locked) {
            handled = super.onInterceptTouchEvent(parent, child, event)
        }
        return handled
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, event: MotionEvent): Boolean {
        var handled = false

        if (!locked) {
            handled = super.onTouchEvent(parent, child, event)
        }

        return handled
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        var handled = false

        if (!locked) {
            handled = super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target, nestedScrollAxes, type)
        }

        return handled
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (!locked) {
            super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
        }
    }

    override fun onStopNestedScroll(coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int) {
        if (!locked) {
            super.onStopNestedScroll(coordinatorLayout, child, target, type)
        }
    }

    override fun onNestedPreFling(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        var handled = false
        if (!locked) handled = super.onNestedPreFling(coordinatorLayout, child, target, velocityX, velocityY)
        return handled
    }
}