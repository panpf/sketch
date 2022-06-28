package com.github.panpf.sketch.target

import android.graphics.drawable.Drawable
import android.widget.RemoteViews
import androidx.annotation.IdRes
import com.github.panpf.sketch.util.toBitmap

class RemoteViewsDisplayTarget(
    private val remoteViews: RemoteViews,
    @IdRes private val imageViewId: Int,
    private val ignoreNullDrawable: Boolean = false,
    private val onUpdated: () -> Unit,
) : DisplayTarget {

    override fun onStart(placeholder: Drawable?) {
        if (placeholder != null || !ignoreNullDrawable) {
            setDrawable(placeholder)
        }
    }

    override fun onError(error: Drawable?) {
        if (error != null || !ignoreNullDrawable) {
            setDrawable(error)
        }
    }

    override fun onSuccess(result: Drawable) = setDrawable(result)

    private fun setDrawable(result: Drawable?) {
        remoteViews.setImageViewBitmap(imageViewId, result?.toBitmap())
        onUpdated()
    }
}
