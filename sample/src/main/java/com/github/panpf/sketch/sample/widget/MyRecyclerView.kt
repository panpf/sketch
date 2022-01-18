package com.github.panpf.sketch.sample.widget

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.RecyclerView
import com.github.panpf.sketch.util.PauseLoadWhenScrollingMixedScrollListener

class MyRecyclerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : RecyclerView(context, attrs) {
    init {
        PauseLoadWhenScrollingMixedScrollListener.attach(this)
    }
}