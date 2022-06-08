package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import androidx.navigation.findNavController
import com.github.panpf.sketch.SketchImageView
import com.github.panpf.sketch.sample.ui.setting.ImageInfoDialogFragment

open class MyImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : SketchImageView(context, attrs, defStyle) {

    init {
        setOnLongClickListener {
            findNavController().navigate(
                ImageInfoDialogFragment.createDirectionsFromImageView(this@MyImageView, null)
            )
            true
        }
    }
}