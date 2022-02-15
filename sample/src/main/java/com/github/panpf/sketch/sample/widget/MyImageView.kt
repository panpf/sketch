package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.format.Formatter
import android.util.AttributeSet
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.github.panpf.activity.monitor.ActivityMonitor
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.request.RequestManagerUtils
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.util.observeFromView
import com.github.panpf.sketch.util.getLastDrawable
import com.github.panpf.sketch.viewability.removeDataFrom
import com.github.panpf.sketch.viewability.showDataFrom

open class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchImageView(context, attrs, defStyle) {
    private val mediatorLiveData = MediatorLiveData<Any>()

    init {
        mediatorLiveData.observeFromView(this) {
            RequestManagerUtils.requestManagerOrNull(this@MyImageView)?.restart()
        }
        context.appSettingsService.apply {
            mediatorLiveData.apply {
                val observer = Observer<Any> {
                    postValue(1)
                }
                addSource(disabledBitmapMemoryCache, observer)
                addSource(disabledNetworkContentDiskCache, observer)
                addSource(disabledBitmapResultDiskCache, observer)
                addSource(disabledBitmapPool, observer)
                addSource(inPreferQualityOverSpeed, observer)
                addSource(bitmapQuality, observer)
            }

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
            append(drawable.imageInfo.toShortString())
            append("\n").append("\n")
            append(drawable.bitmapInfo.toShortString())
            append("\n").append("\n")
            append(
                "transformedList: ${
                    drawable.transformedList?.joinToString {
                        it.toString().replace("Transformed", "")
                    }
                }"
            )
            append("\n").append("\n")
            append(drawable.requestKey)
        }
        AlertDialog.Builder(activity).apply {
            setMessage(message)
            setNegativeButton("Cancel", null)
        }.show()
    }
}