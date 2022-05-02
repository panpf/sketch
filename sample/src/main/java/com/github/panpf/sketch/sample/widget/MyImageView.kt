package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import androidx.navigation.findNavController
import com.github.panpf.liveevent.Listener
import com.github.panpf.liveevent.MediatorLiveEvent
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.sample.appSettingsService
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.observeFromView
import com.github.panpf.sketch.util.SketchUtils

open class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchImageView(context, attrs, defStyle) {
    private val mediatorLiveData = MediatorLiveEvent<Any>()

    init {
        mediatorLiveData.observeFromView(this) {
            SketchUtils.restart(this@MyImageView)
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
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    addSource(colorSpace.liveEvent, observer)
                }
            }
        }

        setOnLongClickListener {
            findNavController().navigate(
                ImageInfoDialogFragment.createDirectionsFromImageView(this@MyImageView, null)
            )
            true
        }
    }
}