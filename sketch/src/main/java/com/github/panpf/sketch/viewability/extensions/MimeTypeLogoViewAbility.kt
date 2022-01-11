package com.github.panpf.sketch.viewability.extensions

import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.util.getLastDrawable
import com.github.panpf.sketch.viewability.DrawAbility
import com.github.panpf.sketch.viewability.RequestListenerAbility
import com.github.panpf.sketch.viewability.ViewAbility
import com.github.panpf.sketch.viewability.ViewAbilityContainerOwner

class MimeTypeLogoViewAbility(
    private val mimeTypeIconMap: Map<String, MimeTypeLogo>,
    private val margin: Int = 0
) : ViewAbility, RequestListenerAbility, DrawAbility {

    override var view: ImageView? = null

    override fun onRequestStart(request: DisplayRequest) {
        view?.postInvalidate()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        view?.postInvalidate()
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
        view?.postInvalidate()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        val view = view ?: return
        val lastDrawable = view.drawable?.getLastDrawable() ?: return
        if (lastDrawable !is SketchDrawable) return
        val mimeType = lastDrawable.mimeType ?: return
        val mimeTypeLogo = mimeTypeIconMap[mimeType] ?: return
        if (mimeTypeLogo.hiddenWhenAnimatable && lastDrawable is Animatable) return
        val logoDrawable = mimeTypeLogo.getDrawable(view.context)
        logoDrawable.setBounds(
            view.right - view.paddingRight - margin - logoDrawable.intrinsicWidth,
            view.bottom - view.paddingBottom - margin - logoDrawable.intrinsicHeight,
            view.right - view.paddingRight - margin,
            view.bottom - view.paddingBottom - margin
        )
        logoDrawable.draw(canvas)
    }

    override fun onDrawForegroundBefore(canvas: Canvas) {

    }

    override fun onDrawForeground(canvas: Canvas) {

    }
}


fun ViewAbilityContainerOwner.setMimeTypeLogo(mimeTypeLogoViewAbility: MimeTypeLogoViewAbility?) {
    val viewAbilityContainer = viewAbilityContainer
    viewAbilityContainer.viewAbilityList
        .find { it is MimeTypeLogoViewAbility }
        ?.let { viewAbilityContainer.removeViewAbility(it) }
    if (mimeTypeLogoViewAbility != null) {
        viewAbilityContainer.addViewAbility(mimeTypeLogoViewAbility)
    }
}

fun ViewAbilityContainerOwner.setMimeTypeLogoWith(
    mimeTypeIconMap: Map<String, MimeTypeLogo>?,
    margin: Int = 0
) {
    val mimeTypeLogoViewAbility = if (mimeTypeIconMap?.isNotEmpty() == true) {
        MimeTypeLogoViewAbility(mimeTypeIconMap, margin)
    } else {
        null
    }
    setMimeTypeLogo(mimeTypeLogoViewAbility)
}

fun ViewAbilityContainerOwner.setMimeTypeLogoWithDrawable(
    mimeTypeIconMap: Map<String, Drawable>?,
    margin: Int = 0
) {
    val mimeTypeLogoViewAbility = if (mimeTypeIconMap?.isNotEmpty() == true) {
        val newMap = mimeTypeIconMap.mapValues {
            MimeTypeLogo(it.value)
        }
        MimeTypeLogoViewAbility(newMap, margin)
    } else {
        null
    }
    setMimeTypeLogo(mimeTypeLogoViewAbility)
}

fun ViewAbilityContainerOwner.setMimeTypeLogoWithResId(
    mimeTypeIconMap: Map<String, Int>?,
    margin: Int = 0
) {
    val mimeTypeLogoViewAbility = if (mimeTypeIconMap?.isNotEmpty() == true) {
        val newMap = mimeTypeIconMap.mapValues {
            MimeTypeLogo(it.value)
        }
        MimeTypeLogoViewAbility(newMap, margin)
    } else {
        null
    }
    setMimeTypeLogo(mimeTypeLogoViewAbility)
}