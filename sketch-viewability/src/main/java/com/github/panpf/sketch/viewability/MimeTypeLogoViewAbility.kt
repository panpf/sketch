package com.github.panpf.sketch.viewability

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.util.getLastDrawable
import com.github.panpf.sketch.viewability.internal.Host
import com.github.panpf.sketch.viewability.internal.ViewAbility
import com.github.panpf.sketch.viewability.internal.ViewAbility.DrawObserver
import com.github.panpf.sketch.viewability.internal.ViewAbility.RequestListenerObserver
import com.github.panpf.sketch.viewability.internal.ViewAbilityContainerOwner

class MimeTypeLogoViewAbility(
    private val mimeTypeIconMap: Map<String, MimeTypeLogo>,
    private val margin: Int = 0
) : ViewAbility, RequestListenerObserver, DrawObserver {

    override var host: Host? = null
        set(value) {
            field = value
            value?.postInvalidate()
        }

    override fun onRequestStart(request: DisplayRequest) {
        host?.postInvalidate()
    }

    override fun onRequestError(request: DisplayRequest, result: Error) {
        host?.postInvalidate()
    }

    override fun onRequestSuccess(request: DisplayRequest, result: Success) {
        host?.postInvalidate()
    }

    override fun onDrawBefore(canvas: Canvas) {

    }

    override fun onDraw(canvas: Canvas) {
        val host = host ?: return
        val lastDrawable = host.drawable?.getLastDrawable() ?: return
        if (lastDrawable !is SketchDrawable) return
        val mimeType = lastDrawable.mimeType ?: return
        val mimeTypeLogo = mimeTypeIconMap[mimeType] ?: return
        if (mimeTypeLogo.hiddenWhenAnimatable && lastDrawable is Animatable) return
        val layoutRect = host.layoutRect
        val paddingRect = host.paddingRect
        val logoDrawable = mimeTypeLogo.getDrawable(host.context)
        logoDrawable.setBounds(
            layoutRect.right - paddingRect.right - margin - logoDrawable.intrinsicWidth,
            layoutRect.bottom - paddingRect.bottom - margin - logoDrawable.intrinsicHeight,
            layoutRect.right - paddingRect.right - margin,
            layoutRect.bottom - paddingRect.bottom - margin
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
        return _drawable ?: if (data is Drawable) {
            _drawable = data
            data
        } else {
            val drawableResId = data as Int
            val newDrawable = ResourcesCompat.getDrawable(context.resources, drawableResId, null)!!
            _drawable = newDrawable
            newDrawable
        }
    }
}