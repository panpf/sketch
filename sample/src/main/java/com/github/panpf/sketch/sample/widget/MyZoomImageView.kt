package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import androidx.navigation.findNavController
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment
import com.github.panpf.sketch.zoom.SketchZoomImageView

class MyZoomImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchZoomImageView(context, attrs, defStyle) {

    init {
        setOnLongClickListener {
            findNavController().navigate(
                ImageInfoDialogFragment.createDirectionsFromImageView(this@MyZoomImageView, null)
            )
            true
        }
    }
}