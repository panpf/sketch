package com.github.panpf.sketch.request

import android.graphics.drawable.Animatable2
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import androidx.annotation.RequiresApi
import androidx.vectordrawable.graphics.drawable.Animatable2Compat

/** Pass this to [repeatCount] to repeat infinitely. */
const val ANIMATION_REPEAT_INFINITE = -1
const val ANIMATION_REPEAT_COUNT_KEY = "sketch#animation_repeat_count"
const val ANIMATION_START_CALLBACK_KEY = "sketch#animation_start_callback"
const val ANIMATION_END_CALLBACK_KEY = "sketch#animation_end_callback"

/**
 * Set the number of times to repeat the animation if the result is an animated [Drawable].
 *
 * @see AnimatedImageDrawable.setRepeatCount
 */
fun DisplayRequest.Builder.repeatCount(repeatCount: Int): DisplayRequest.Builder {
    require(repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
    return setParameter(ANIMATION_REPEAT_COUNT_KEY, repeatCount)
}

/**
 * Get the number of times to repeat the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.repeatCount(): Int? = parameters?.value(ANIMATION_REPEAT_COUNT_KEY)

/**
 * Set the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.Builder.onAnimationStart(callback: (() -> Unit)?): DisplayRequest.Builder {
    return setParameter(ANIMATION_START_CALLBACK_KEY, callback)
}

/**
 * Get the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.animationStartCallback(): (() -> Unit)? =
    parameters?.value(ANIMATION_START_CALLBACK_KEY)

/**
 * Set the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.Builder.onAnimationEnd(callback: (() -> Unit)?): DisplayRequest.Builder {
    return setParameter(ANIMATION_END_CALLBACK_KEY, callback)
}

/**
 * Get the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
fun DisplayRequest.animationEndCallback(): (() -> Unit)? =
    parameters?.value(ANIMATION_END_CALLBACK_KEY)



@RequiresApi(23)
fun animatable2CallbackOf(
    onStart: (() -> Unit)?,
    onEnd: (() -> Unit)?
) = object : Animatable2.AnimationCallback() {
    override fun onAnimationStart(drawable: Drawable?) {
        onStart?.invoke()
    }

    override fun onAnimationEnd(drawable: Drawable?) {
        onEnd?.invoke()
    }
}

fun animatable2CompatCallbackOf(
    onStart: (() -> Unit)?,
    onEnd: (() -> Unit)?
) = object : Animatable2Compat.AnimationCallback() {
    override fun onAnimationStart(drawable: Drawable?) {
        onStart?.invoke()
    }

    override fun onAnimationEnd(drawable: Drawable?) {
        onEnd?.invoke()
    }
}
