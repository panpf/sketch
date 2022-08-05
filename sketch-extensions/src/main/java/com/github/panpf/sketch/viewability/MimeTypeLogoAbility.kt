/*
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
package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import com.github.panpf.sketch.util.findLastSketchDrawable

/**
 * Display the MimeType logo in the lower right corner of the View
 */
fun ViewAbilityContainer.showMimeTypeLogoWithDrawable(
    mimeTypeIconMap: Map<String, Drawable>,
    margin: Int = 0
) {
    removeMimeTypeLogo()
    addViewAbility(
        MimeTypeLogoAbility(
            mimeTypeIconMap.mapValues { MimeTypeLogo(it.value) },
            margin
        )
    )
}

/**
 * Display the MimeType logo in the lower right corner of the View
 */
fun ViewAbilityContainer.showMimeTypeLogoWithRes(
    mimeTypeIconMap: Map<String, Int>,
    margin: Int = 0
) {
    removeMimeTypeLogo()
    addViewAbility(
        MimeTypeLogoAbility(
            mimeTypeIconMap.mapValues { MimeTypeLogo(it.value) },
            margin
        )
    )
}

/**
 * Remove MimeType logo
 */
fun ViewAbilityContainer.removeMimeTypeLogo() {
    viewAbilityList
        .find { it is MimeTypeLogoAbility }
        ?.let { removeViewAbility(it) }
}

/**
 * Returns true if MimeType logo feature is enabled
 */
val ViewAbilityContainer.isShowMimeTypeLogo: Boolean
    get() = viewAbilityList.find { it is MimeTypeLogoAbility } != null


class MimeTypeLogoAbility(
    private val mimeTypeIconMap: Map<String, MimeTypeLogo>,
    private val margin: Int = 0
) : ViewAbility, AttachObserver, DrawObserver, LayoutObserver, DrawableObserver {

    override var host: Host? = null
    private var logoDrawable: Drawable? = null

    override fun onAttachedToWindow() {
        reset()
        host?.view?.invalidate()
    }

    override fun onDetachedFromWindow() {

    }

    override fun onDrawableChanged(oldDrawable: Drawable?, newDrawable: Drawable?) {
        reset()
        host?.view?.invalidate()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        reset()
        host?.view?.invalidate()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        logoDrawable?.draw(canvas)
    }

    private fun reset() {
        logoDrawable = null
        val host = host ?: return
        val view = host.view
        val lastDrawable = host.container.getDrawable()?.findLastSketchDrawable() ?: return
        val mimeType = lastDrawable.imageInfo.mimeType
        val mimeTypeLogo = mimeTypeIconMap[mimeType] ?: return
        if (mimeTypeLogo.hiddenWhenAnimatable && lastDrawable is Animatable) return
        val logoDrawable = mimeTypeLogo.getDrawable(host.context)
        logoDrawable.setBounds(
            view.right - view.paddingRight - margin - logoDrawable.intrinsicWidth,
            view.bottom - view.paddingBottom - margin - logoDrawable.intrinsicHeight,
            view.right - view.paddingRight - margin,
            view.bottom - view.paddingBottom - margin
        )
        this.logoDrawable = logoDrawable
    }
}

class MimeTypeLogo {

    private val data: Any
    private var _drawable: Drawable? = null

    val hiddenWhenAnimatable: Boolean

    constructor(drawable: Drawable, hiddenWhenAnimatable: Boolean = false) {
        this.data = drawable
        this.hiddenWhenAnimatable = hiddenWhenAnimatable
    }

    constructor(drawableResId: Int, hiddenWhenAnimatable: Boolean = false) {
        this.data = drawableResId
        this.hiddenWhenAnimatable = hiddenWhenAnimatable
    }

    fun getDrawable(context: Context): Drawable {
        return _drawable
            ?: if (data is Drawable) {
                data
            } else {
                AppCompatResources.getDrawable(context, data as Int)!!
            }.apply {
                _drawable = this
            }
    }
}