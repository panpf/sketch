package com.github.panpf.sketch

import android.widget.ImageView
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult
import com.github.panpf.sketch.request.Disposable

/**
 * Load the icon of the installed app and and display it on this [ImageView]
 *
 * You can set request params with a trailing lambda function [configBlock]
 */
fun ImageView.displayAppIconImage(
    packageName: String,
    versionCode: Int,
    configBlock: (DisplayRequest.Builder.() -> Unit)? = null
): Disposable<DisplayResult> =
    DisplayRequest(this, newAppIconUri(packageName, versionCode), configBlock).enqueue()