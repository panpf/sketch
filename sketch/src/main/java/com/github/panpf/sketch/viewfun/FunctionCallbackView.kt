/*
 * Copyright (C) 2019 panpf <panpfpanpf@outlook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.panpf.sketch.viewfun

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.github.panpf.sketch.SketchView
import com.github.panpf.sketch.request.DisplayCache
import com.github.panpf.sketch.request.DisplayListener
import com.github.panpf.sketch.request.DisplayOptions
import com.github.panpf.sketch.request.DownloadProgressListener

/**
 * 这个类负责给 function 回调各种状态
 */
abstract class FunctionCallbackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : ImageView(context, attrs, defStyle), SketchView {

    private val progressListenerProxy: ProgressListenerProxy = ProgressListenerProxy(this)
    private val displayListenerProxy: DisplayListenerProxy = DisplayListenerProxy(this)
    private val clickListenerProxy: OnClickListenerProxy = OnClickListenerProxy(this)

    var wrappedClickListener: OnClickListener? = null
    var longClickListener: OnLongClickListener? = null

    @JvmField
    var wrappedDisplayListener: DisplayListener? = null
    var wrappedProgressListener: DownloadProgressListener? = null

    /* 为什么要搞成延迟 new 的？因为父类会第一时间调用 setDrawable() 方法，
        这个方法里需要用到 functions，即使直接 ViewFunctions functions = new ViewFunctions(this); 都不行  */
    private var functionsCache: ViewFunctions? = null
    val functions: ViewFunctions
        get() {
            /* 为什么要搞成延迟 new 的？因为父类会第一时间调用 setDrawable() 方法，
        这个方法里需要用到 functions，即使直接 ViewFunctions functions = new ViewFunctions(this); 都不行  */
            if (functionsCache == null) {
                synchronized(this) {
                    if (functionsCache == null) {
                        functionsCache = ViewFunctions(this)
                    }
                }
            }
            return functionsCache!!
        }

    override var options: DisplayOptions
        get() = functions.requestFunction.displayOptions
        set(newDisplayOptions) {
            functions.requestFunction.displayOptions.copy(newDisplayOptions)
        }
    override var displayListener: DisplayListener?
        get() = displayListenerProxy
        set(displayListener) {
            wrappedDisplayListener = displayListener
        }
    override var downloadProgressListener: DownloadProgressListener?
        get() = if (functions.showDownloadProgressFunction != null || wrappedProgressListener != null) {
            progressListenerProxy
        } else {
            null
        }
        set(downloadProgressListener) {
            wrappedProgressListener = downloadProgressListener
        }
    override var displayCache: DisplayCache?
        get() = functions.requestFunction.displayCache
        set(value) {
            functions.requestFunction.displayCache = value
        }

    override val isUseSmallerThumbnails: Boolean
        get() = false

    init {
        super.setOnClickListener(clickListenerProxy)
        updateClickable()
    }

    fun addViewFunction(viewFunction: ViewFunction, priority: Int) {
        functions.addViewFunction(viewFunction, priority)
    }

    fun removeViewFunction(viewFunction: ViewFunction) {
        functions.removeViewFunction(viewFunction)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        functions.onLayout(changed, left, top, right, bottom)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        functions.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        functions.onDraw(canvas)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return functions.onTouchEvent(event) || super.onTouchEvent(event)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        functions.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (functions.onDetachedFromWindow()) {
            super.setImageDrawable(null)
        }
    }

    fun getOnClickListener(): OnClickListener {
        return clickListenerProxy
    }

    override fun setOnClickListener(l: OnClickListener) {
        wrappedClickListener = l
        updateClickable()
    }

    fun getOnLongClickListener(): OnLongClickListener? {
        return longClickListener
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        super.setOnLongClickListener(l)
        longClickListener = l
    }

    fun updateClickable() {
        isClickable = clickListenerProxy.isClickable
    }

    override fun setImageURI(uri: Uri?) {
        val oldDrawable = drawable
        super.setImageURI(uri)
        val newDrawable = drawable
        setDrawable("setImageURI", oldDrawable, newDrawable)
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        val oldDrawable = drawable
        super.setImageResource(resId)
        val newDrawable = drawable
        setDrawable("setImageResource", oldDrawable, newDrawable)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        val oldDrawable = getDrawable()
        super.setImageDrawable(drawable)
        val newDrawable = getDrawable()
        setDrawable("setImageDrawable", oldDrawable, newDrawable)
    }

    private fun setDrawable(callPosition: String, oldDrawable: Drawable?, newDrawable: Drawable?) {
        if (newDrawable == null) {
            functions.requestFunction.clean()
        }
        if (oldDrawable !== newDrawable) {
            if (functions.onDrawableChanged(callPosition, oldDrawable, newDrawable)) {
                invalidate()
            }
        }
    }

    override fun onReadyDisplay(uri: String) {
        if (functions.onReadyDisplay(uri)) {
            invalidate()
        }
    }
}