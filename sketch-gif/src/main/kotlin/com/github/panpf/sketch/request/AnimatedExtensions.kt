/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ------------------------------------------------------------------------
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.request

import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import androidx.vectordrawable.graphics.drawable.Animatable2Compat
import com.github.panpf.sketch.transform.AnimatedTransformation

/** Pass this to [repeatCount] to repeat infinitely. */
const val ANIMATION_REPEAT_INFINITE = -1
const val ANIMATION_REPEAT_COUNT_KEY = "sketch#animation_repeat_count"
const val ANIMATION_START_CALLBACK_KEY = "sketch#animation_start_callback"
const val ANIMATION_END_CALLBACK_KEY = "sketch#animation_end_callback"
const val ANIMATED_TRANSFORMATION_KEY = "sketch#animated_transformation"

/**
 * Set the number of times to repeat the animation if the result is an animated [Drawable].
 *
 * @see AnimatedImageDrawable.setRepeatCount
 */
fun ImageRequest.Builder.repeatCount(repeatCount: Int): ImageRequest.Builder {
    require(repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
    return setParameter(ANIMATION_REPEAT_COUNT_KEY, repeatCount, null)
}

///**
// * Set the number of times to repeat the animation if the result is an animated [Drawable].
// *
// * @see AnimatedImageDrawable.setRepeatCount
// */
//fun DisplayRequest.Builder.repeatCount(repeatCount: Int): DisplayRequest.Builder {
//    require(repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
//    return setParameter(ANIMATION_REPEAT_COUNT_KEY, repeatCount, null)
//}

/**
 * Get the number of times to repeat the animation if the result is an animated [Drawable].
 */
val ImageRequest.repeatCount: Int?
    get() = parameters?.value(ANIMATION_REPEAT_COUNT_KEY)

/**
 * Set the number of times to repeat the animation if the result is an animated [Drawable].
 *
 * @see AnimatedImageDrawable.setRepeatCount
 */
fun ImageOptions.Builder.repeatCount(repeatCount: Int): ImageOptions.Builder {
    require(repeatCount >= ANIMATION_REPEAT_INFINITE) { "Invalid repeatCount: $repeatCount" }
    return setParameter(ANIMATION_REPEAT_COUNT_KEY, repeatCount, null)
}

/**
 * Get the number of times to repeat the animation if the result is an animated [Drawable].
 */
val ImageOptions.repeatCount: Int?
    get() = parameters?.value(ANIMATION_REPEAT_COUNT_KEY)


/**
 * Set the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
fun ImageRequest.Builder.onAnimationStart(callback: (() -> Unit)?): ImageRequest.Builder {
    return setParameter(ANIMATION_START_CALLBACK_KEY, callback, null)
}

///**
// * Set the callback to be invoked at the start of the animation if the result is an animated [Drawable].
// */
//fun DisplayRequest.Builder.onAnimationStart(callback: (() -> Unit)?): DisplayRequest.Builder {
//    return setParameter(ANIMATION_START_CALLBACK_KEY, callback, null)
//}

/**
 * Get the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
val ImageRequest.animationStartCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_START_CALLBACK_KEY)

/**
 * Set the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
fun ImageOptions.Builder.onAnimationStart(callback: (() -> Unit)?): ImageOptions.Builder {
    return setParameter(ANIMATION_START_CALLBACK_KEY, callback, null)
}

/**
 * Get the callback to be invoked at the start of the animation if the result is an animated [Drawable].
 */
val ImageOptions.animationStartCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_START_CALLBACK_KEY)


/**
 * Set the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
fun ImageRequest.Builder.onAnimationEnd(callback: (() -> Unit)?): ImageRequest.Builder {
    return setParameter(ANIMATION_END_CALLBACK_KEY, callback, null)
}

///**
// * Set the callback to be invoked at the end of the animation if the result is an animated [Drawable].
// */
//fun DisplayRequest.Builder.onAnimationEnd(callback: (() -> Unit)?): DisplayRequest.Builder {
//    return setParameter(ANIMATION_END_CALLBACK_KEY, callback, null)
//}

/**
 * Get the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
val ImageRequest.animationEndCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_END_CALLBACK_KEY)

/**
 * Set the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
fun ImageOptions.Builder.onAnimationEnd(callback: (() -> Unit)?): ImageOptions.Builder {
    return setParameter(ANIMATION_END_CALLBACK_KEY, callback, null)
}

/**
 * Get the callback to be invoked at the end of the animation if the result is an animated [Drawable].
 */
val ImageOptions.animationEndCallback: (() -> Unit)?
    get() = parameters?.value(ANIMATION_END_CALLBACK_KEY)


/**
 * Set the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * Default: `null`
 */
fun ImageRequest.Builder.animatedTransformation(animatedTransformation: AnimatedTransformation): ImageRequest.Builder {
    return setParameter(ANIMATED_TRANSFORMATION_KEY, animatedTransformation, null)
}

///**
// * Set the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
// *
// * Default: `null`
// */
//fun DisplayRequest.Builder.animatedTransformation(animatedTransformation: AnimatedTransformation): DisplayRequest.Builder {
//    return setParameter(ANIMATED_TRANSFORMATION_KEY, animatedTransformation, null)
//}

/**
 * Get the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 */
val ImageRequest.animatedTransformation: AnimatedTransformation?
    get() = parameters?.value(ANIMATED_TRANSFORMATION_KEY)

/**
 * Set the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 *
 * Default: `null`
 */
fun ImageOptions.Builder.animatedTransformation(animatedTransformation: AnimatedTransformation): ImageOptions.Builder {
    return setParameter(ANIMATED_TRANSFORMATION_KEY, animatedTransformation, null)
}

/**
 * Get the [AnimatedTransformation] that will be applied to the result if it is an animated [Drawable].
 */
val ImageOptions.animatedTransformation: AnimatedTransformation?
    get() = parameters?.value(ANIMATED_TRANSFORMATION_KEY)


//@RequiresApi(23)
//fun animatable2CallbackOf(
//    onStart: (() -> Unit)?,
//    onEnd: (() -> Unit)?
//) = object : Animatable2.AnimationCallback() {
//    override fun onAnimationStart(drawable: Drawable?) {
//        onStart?.invoke()
//    }
//
//    override fun onAnimationEnd(drawable: Drawable?) {
//        onEnd?.invoke()
//    }
//}

fun animatable2CompatCallbackOf(
    onStart: (() -> Unit)?,
    onEnd: (() -> Unit)?
): Animatable2Compat.AnimationCallback = object : Animatable2Compat.AnimationCallback() {
    override fun onAnimationStart(drawable: Drawable?) {
        onStart?.invoke()
    }

    override fun onAnimationEnd(drawable: Drawable?) {
        onEnd?.invoke()
    }
}
