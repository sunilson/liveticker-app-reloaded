package at.sunilson.liveticker.presentation

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.ScaleAnimation
import timber.log.Timber


/**
 * Circular-reveals everything around the target View in or out, depending on the start and end radius
 */
fun View.circularReveal(
    centerX: Int,
    centerY: Int,
    startRadius: Float,
    endRadius: Float,
    duration: Long = 300,
    finished: () -> Unit
) {

    Timber.d("Circular reveal for $this with center: $centerX:$centerY and startRadius $startRadius and endRadius $endRadius and duration $duration")

    ViewAnimationUtils.createCircularReveal(this, centerX, centerY, startRadius, endRadius).apply {
        interpolator = AccelerateInterpolator()
        this.duration = duration
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                finished()
                if (startRadius != 0f) visibility = View.GONE
            }

            override fun onAnimationCancel(animation: Animator?) {
                finished()
            }

            override fun onAnimationStart(animation: Animator?) {}
        })
        if (startRadius == 0f) visibility = View.VISIBLE
        start()
    }
}

fun View.scaleUpViewFromCenter(up: Boolean, endValue: Float = 1.05f, duration: Long = 300L) {
    val animation = ScaleAnimation(
        if (up) 1f else endValue,
        if (up) endValue else 1f,
        if (up) 1f else endValue,
        if (up) endValue else 1f,
        Animation.RELATIVE_TO_SELF,
        0.5f,
        Animation.RELATIVE_TO_SELF,
        0.5f
    )
    animation.fillAfter = true
    animation.duration = duration
    startAnimation(animation)
}

/**
 * Sets alpha of all children of given [View] to 0 and then animates their alpha to 1 and translation from the bottom
 * to their actual position.
 *
 * @param delay Delay between each childrens entry animation
 * @param duration Duration of each entry animation
 * @param view [ViewGroup] that contains the children that should be animated
 * @return Callback that is called when the animation has been finished
 */
fun ViewGroup.enterChildViewsFromBottomDelayed(
    delay: Long = 100,
    duration: Long = 500,
    initialDelay: Long = 0,
    childMax: Int = -1,
    finished: () -> Unit = {}
) {
    for (i in 0 until childCount) {
        if (childMax == -1 || i < childMax) getChildAt(i).alpha = 0f
    }

    val animators = (0 until childCount).mapIndexed { index, value ->
        if (childMax != -1 && index >= childMax) return@mapIndexed null
        AnimatorSet().apply {
            playTogether(
                ObjectAnimator.ofFloat(getChildAt(index), "translationY", 50f, 0f),
                ObjectAnimator.ofFloat(getChildAt(index), "alpha", 0f, 1.0f)
            )
            startDelay = index * delay
            this.duration = duration
            interpolator = DecelerateInterpolator()
        }
    }.filterNotNull()

    AnimatorSet().apply {
        startDelay = initialDelay
        playTogether(animators)
        addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}
            override fun onAnimationEnd(animation: Animator?) {
                finished()
            }

            override fun onAnimationCancel(animation: Animator?) {
                finished()
            }

            override fun onAnimationStart(animation: Animator?) {}

        })
    }.start()
}