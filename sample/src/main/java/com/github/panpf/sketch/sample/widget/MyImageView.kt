package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.util.AttributeSet
import androidx.appcompat.app.AlertDialog
import com.github.panpf.activity.monitor.ActivityMonitor
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.decode.internal.ExifOrientationCorrector
import com.github.panpf.sketch.drawable.SketchBitmapDrawable
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.drawable.SketchGifDrawable
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.util.observeFromView
import com.github.panpf.sketch.util.byteCountCompat
import com.github.panpf.sketch.util.getLastDrawable
import com.github.panpf.sketch.viewability.removeDataFrom
import com.github.panpf.sketch.viewability.showDataFrom

open class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchImageView(context, attrs, defStyle) {

    init {
        context.appSettingsService.apply {
            showDataFrom.observeFromView(this@MyImageView) {
                if (it == true) {
                    showDataFrom()
                } else {
                    removeDataFrom()
                }
            }
        }

        setOnLongClickListener {
            val drawable = drawable?.getLastDrawable()
            drawable?.let { it1 -> showDrawableInfo(it1) }
            drawable != null
        }
    }

    private fun showDrawableInfo(drawable: Drawable) {
        val activity = ActivityMonitor.getLastResumedActivity() ?: return
        if (drawable !is SketchDrawable) return
        val message = buildString {
            append(
                "image: ${
                    drawable.run {
                        val size = "${originWidth}x${originHeight}"
                        val exifOrieName = ExifOrientationCorrector.toName(exifOrientation)
                        "$size, ${mimeType}, ${exifOrieName}"
                    }
                }"
            )
            if (drawable is SketchBitmapDrawable) {
                append("\n").append("\n")
                append(
                    "bitmap: ${
                        drawable.run {
                            val byteCount = bitmap.byteCountCompat
                            val size = Formatter.formatFileSize(activity, byteCount.toLong())
                            "${bitmap.width}x${bitmap.height}, ${bitmap.config}, $size, ${dataFrom}"
                        }
                    }"
                )
            } else if (drawable is SketchGifDrawable) {
                append("\n").append("\n")
                append("gif: ${
                    drawable.run {
                        val bitmap = getCurrentFrame()
                        val byteCount = bitmap?.byteCountCompat ?: 0
                        val size = Formatter.formatFileSize(activity, byteCount.toLong())
                        "${bitmap?.width}x${bitmap?.height}, ${bitmap?.config}, $size, ${dataFrom}"
                    }
                }")
            }
            append("\n").append("\n")
            append(drawable.key)
        }
        AlertDialog.Builder(activity).apply {
            setMessage(message)
            setNegativeButton("Cancel", null)
        }.show()
    }
}