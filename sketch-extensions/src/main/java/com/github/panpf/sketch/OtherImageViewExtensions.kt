package com.github.panpf.sketch

import android.widget.ImageView
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable

fun ImageView.displayAppIconImage(
    packageName: String,
    versionCode: Int,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    context.sketch
        .enqueue(DisplayRequest(this, newAppIconUri(packageName, versionCode), configBlock))