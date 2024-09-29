package com.github.panpf.sketch.view.core.test.request

import android.widget.ImageView
import com.github.panpf.sketch.request.ImageOptions
import com.github.panpf.sketch.request.sizeWithView
import com.github.panpf.sketch.resize.internal.ViewSizeResolver
import com.github.panpf.sketch.test.utils.getTestContext
import kotlin.test.Test
import kotlin.test.assertEquals

class ImageOptionsViewTest {

    @Test
    fun testSizeWithView() {
        val context = getTestContext()
        val imageView = ImageView(context)

        ImageOptions {
            sizeWithView(imageView)
        }.apply {
            assertEquals(
                expected = ViewSizeResolver(imageView, subtractPadding = true),
                actual = this.sizeResolver
            )
        }

        ImageOptions {
            sizeWithView(imageView, subtractPadding = false)
        }.apply {
            assertEquals(
                expected = ViewSizeResolver(imageView, subtractPadding = false),
                actual = this.sizeResolver
            )
        }
    }
}