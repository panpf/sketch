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
package com.github.panpf.sketch.ability

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import com.github.panpf.sketch.drawable.DrawableFetcher
import com.github.panpf.sketch.drawable.RealDrawable
import com.github.panpf.sketch.drawable.ResDrawable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
            mimeTypeIconMap.mapValues { RealDrawable(it.value) },
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
            mimeTypeIconMap.mapValues { ResDrawable(it.value) },
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
    private val mimeTypeIconMap: Map<String, DrawableFetcher>,
    private val margin: Int = 0
) : ViewAbility, AttachObserver, DrawObserver, LayoutObserver, DrawableObserver {

    override var host: Host? = null
    private var logoDrawable: Drawable? = null
    private var coroutineScope: CoroutineScope? = null

    override fun onAttachedToWindow() {
        val coroutineScope = CoroutineScope(Dispatchers.Main)
        this.coroutineScope = coroutineScope
        val host = host!!
        coroutineScope.launch {
            host.container.requestState.loadState.collectLatest {
                reset()
                host.view.invalidate()
            }
        }
    }

    override fun onDetachedFromWindow() {
        coroutineScope?.cancel()
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

    private fun reset(): Boolean {
        logoDrawable = null
        val host = host ?: return false
        host.container.getDrawable() ?: return false
        val view = host.view
        val result = host.container.requestState.resultState.value
        val mimeType = getMimeTypeFromImageResult(result) ?: return false
        val mimeTypeLogo = mimeTypeIconMap[mimeType] ?: return false
        val logoDrawable = mimeTypeLogo.getDrawable(host.context)
        logoDrawable.setBounds(
            view.right - view.paddingRight - margin - logoDrawable.intrinsicWidth,
            view.bottom - view.paddingBottom - margin - logoDrawable.intrinsicHeight,
            view.right - view.paddingRight - margin,
            view.bottom - view.paddingBottom - margin
        )
        this.logoDrawable = logoDrawable
        return true
    }
}