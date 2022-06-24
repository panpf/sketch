package com.github.panpf.sketch.test.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener
import com.github.panpf.sketch.target.ListenerProvider

class TestListenerImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ImageView(context, attrs), ListenerProvider {

    override fun getListener(): Listener<DisplayRequest, Success, Error> {
        return object : Listener<DisplayRequest, Success, Error> {

        }
    }

    override fun getProgressListener(): ProgressListener<DisplayRequest> {
        return ProgressListener { _, _, _ -> }
    }
}