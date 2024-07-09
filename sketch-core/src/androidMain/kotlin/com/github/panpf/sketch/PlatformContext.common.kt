package com.github.panpf.sketch

import android.app.Activity

actual typealias PlatformContext = android.content.Context

actual fun checkPlatformContext(context: PlatformContext) {
    require(context !is Activity) {
        "The context cannot be an Activity"
    }
}