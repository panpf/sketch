package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.app.AlertDialog
import com.github.panpf.activity.monitor.ActivityMonitor
import com.github.panpf.liveevent.Listener
import com.github.panpf.liveevent.MediatorLiveEvent
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.decode.internal.exifOrientationName
import com.github.panpf.sketch.drawable.SketchDrawable
import com.github.panpf.sketch.request.RequestManagerUtils
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.util.observeFromView
import com.github.panpf.sketch.util.findLastSketchDrawable
import com.github.panpf.sketch.viewability.removeDataFromLogo
import com.github.panpf.sketch.viewability.showDataFromLogo

open class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchImageView(context, attrs, defStyle) {
    private val mediatorLiveData = MediatorLiveEvent<Any>()

    init {
        mediatorLiveData.observeFromView(this) {
            RequestManagerUtils.requestManagerOrNull(this@MyImageView)?.restart()
        }
        context.appSettingsService.apply {
            mediatorLiveData.apply {
                val observer = Listener<Any> {
                    postValue(1)
                }
                addSource(disabledBitmapMemoryCache.liveEvent, observer)
                addSource(disabledDownloadDiskCache.liveEvent, observer)
                addSource(disabledBitmapResultDiskCache.liveEvent, observer)
                addSource(disabledReuseBitmap.liveEvent, observer)
                addSource(inPreferQualityOverSpeed.liveEvent, observer)
                addSource(bitmapQuality.liveEvent, observer)
            }

            showDataFromLogo.observeFromView(this@MyImageView) {
                if (it == true) {
                    showDataFromLogo()
                } else {
                    removeDataFromLogo()
                }
            }
        }

        setOnLongClickListener {
            val drawable = drawable?.findLastSketchDrawable()
            drawable?.let { it1 -> showDrawableInfo(it1) }
            drawable != null
        }
    }

    private fun showDrawableInfo(drawable: SketchDrawable) {
        val activity = ActivityMonitor.getLastResumedActivity() ?: return
        val message = buildString {
            append("${drawable.imageInfo.toShortString()}/${exifOrientationName(drawable.imageExifOrientation)}")
            append("\n").append("\n")
            append(drawable.bitmapInfo.toShortString())
            append("\n").append("\n")
            val transformedListString = drawable.transformedList?.let { list ->
                when {
                    list.size > 1 -> {
                        list.joinToString(separator = "\n") { transformed ->
                            "   ${transformed.toString().replace("Transformed", "")}"
                        }.let {
                            "\n$it\n"
                        }
                    }
                    list.size == 1 -> {
                        list.joinToString { transformed ->
                            transformed.toString().replace("Transformed", "")
                        }
                    }
                    else -> {
                        null
                    }
                }
            }
            append("TransformedList(${transformedListString})/${drawable.dataFrom}")
            append("\n").append("\n")
            append(drawable.requestKey)
        }
        AlertDialog.Builder(activity).apply {
            setMessage(message)
            setNegativeButton("Cancel", null)
        }.show()
    }
}