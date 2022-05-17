package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import androidx.navigation.findNavController
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.sample.prefsService
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.sample.util.observeWithViewLifecycle
import com.github.panpf.sketch.util.SketchUtils
import kotlinx.coroutines.flow.merge

open class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchImageView(context, attrs, defStyle) {

    init {
        context.prefsService.apply {
            merge(
                disabledBitmapMemoryCache.sharedFlow,
                disabledDownloadDiskCache.sharedFlow,
                disabledBitmapResultDiskCache.sharedFlow,
                disabledReuseBitmap.sharedFlow,
                inPreferQualityOverSpeed.sharedFlow,
                bitmapQuality.sharedFlow,
            ).observeWithViewLifecycle(this@MyImageView) {
                it
                SketchUtils.restart(this@MyImageView)
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