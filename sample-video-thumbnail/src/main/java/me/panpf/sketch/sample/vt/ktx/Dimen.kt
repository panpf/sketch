package me.panpf.sketch.sample.vt.ktx

import android.content.Context

fun Int.dp2px(context: Context) = (this * context.resources.displayMetrics.density + 0.5).toInt()

fun Context.dp2px(dpValue: Int) = (dpValue * resources.displayMetrics.density + 0.5).toInt()

fun Int.px2dp(context: Context): Int {
    return (this / context.resources.displayMetrics.density + 0.5f).toInt()
}

fun Context.px2dp(pxValued: Int): Int {
    return (pxValued / this.resources.displayMetrics.density + 0.5f).toInt()
}