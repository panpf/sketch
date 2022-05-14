package com.github.panpf.sketch.test.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.ImageOptionsProvider

class TestImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ImageView(context, attrs), ImageOptionsProvider {

    override var displayImageOptions: ImageOptions? = null
}