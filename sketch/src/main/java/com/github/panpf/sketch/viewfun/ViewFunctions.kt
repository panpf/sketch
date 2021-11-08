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

import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.view.MotionEvent
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.request.CancelCause
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.ImageFrom
import java.util.*

class ViewFunctions(view: FunctionCallbackView) {
    @JvmField
    var requestFunction: RequestFunction = RequestFunction(view)

    @JvmField
    var showImageFromFunction: ShowImageFromFunction? = null

    @JvmField
    var showDownloadProgressFunction: ShowDownloadProgressFunction? = null

    @JvmField
    var showPressedFunction: ShowPressedFunction? = null

    @JvmField
    var showGifFlagFunction: ShowGifFlagFunction? = null

    @JvmField
    var clickRetryFunction: ClickRetryFunction? = null

    @JvmField
    var clickPlayGifFunction: ClickPlayGifFunction? = null
    private val recyclerCompatFunction: RecyclerCompatFunction = RecyclerCompatFunction(view)
    private val viewFunctions: MutableList<ViewFunctionItem> = LinkedList()

    fun addViewFunction(viewFunction: ViewFunction, priority: Int) {
        viewFunctions.add(ViewFunctionItem(priority, viewFunction))
        viewFunctions.sortWith { o1, o2 -> -1 * (o1.priority - o2.priority) }
    }

    fun removeViewFunction(viewFunction: ViewFunction) {
        val iterator = viewFunctions.iterator()
        while (iterator.hasNext()) {
            val functionItem = iterator.next()
            if (functionItem.function == viewFunction) {
                iterator.remove()
            }
        }
    }

    fun onAttachedToWindow() {
        requestFunction.onAttachedToWindow()
        recyclerCompatFunction.onAttachedToWindow()
        if (showPressedFunction != null) {
            showPressedFunction?.onAttachedToWindow()
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction?.onAttachedToWindow()
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction?.onAttachedToWindow()
        }
        if (showImageFromFunction != null) {
            showImageFromFunction?.onAttachedToWindow()
        }
        if (clickRetryFunction != null) {
            clickRetryFunction?.onAttachedToWindow()
        }
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction?.onAttachedToWindow()
        }
        viewFunctions.forEach { viewFunctionItem ->
            viewFunctionItem.function.onAttachedToWindow()
        }
    }

    fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (showImageFromFunction != null) {
            showImageFromFunction?.onLayout(changed, left, top, right, bottom)
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction?.onLayout(changed, left, top, right, bottom)
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction?.onLayout(changed, left, top, right, bottom)
        }
        if (showPressedFunction != null) {
            showPressedFunction?.onLayout(changed, left, top, right, bottom)
        }
        if (clickRetryFunction != null) {
            clickRetryFunction?.onLayout(changed, left, top, right, bottom)
        }
        requestFunction.onLayout(changed, left, top, right, bottom)
        recyclerCompatFunction.onLayout(changed, left, top, right, bottom)
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction?.onLayout(changed, left, top, right, bottom)
        }
        for (viewFunctionItem in viewFunctions) {
            viewFunctionItem.function.onLayout(changed, left, top, right, bottom)
        }
    }

    fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        requestFunction.onSizeChanged(w, h, oldw, oldh)
        recyclerCompatFunction.onSizeChanged(w, h, oldw, oldh)
        if (showPressedFunction != null) {
            showPressedFunction?.onSizeChanged(w, h, oldw, oldh)
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction?.onSizeChanged(w, h, oldw, oldh)
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction?.onSizeChanged(w, h, oldw, oldh)
        }
        if (showImageFromFunction != null) {
            showImageFromFunction?.onSizeChanged(w, h, oldw, oldh)
        }
        if (clickRetryFunction != null) {
            clickRetryFunction?.onSizeChanged(w, h, oldw, oldh)
        }
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction?.onSizeChanged(w, h, oldw, oldh)
        }
        for (viewFunctionItem in viewFunctions) {
            viewFunctionItem.function.onSizeChanged(w, h, oldw, oldh)
        }
    }

    fun onDraw(canvas: Canvas) {
        if (showPressedFunction != null) {
            showPressedFunction?.onDraw(canvas)
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction?.onDraw(canvas)
        }
        if (showImageFromFunction != null) {
            showImageFromFunction?.onDraw(canvas)
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction?.onDraw(canvas)
        }
        if (clickRetryFunction != null) {
            clickRetryFunction?.onDraw(canvas)
        }
        requestFunction.onDraw(canvas)
        recyclerCompatFunction.onDraw(canvas)
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction?.onDraw(canvas)
        }
        for (viewFunctionItem in viewFunctions) {
            viewFunctionItem.function.onDraw(canvas)
        }
    }

    /**
     * @return true：事件已处理
     */
    fun onTouchEvent(event: MotionEvent): Boolean {
        if (showPressedFunction?.onTouchEvent(event) == true) {
            return true
        }
        if (showDownloadProgressFunction?.onTouchEvent(event) == true) {
            return true
        }
        if (showImageFromFunction?.onTouchEvent(event) == true) {
            return true
        }
        if (showGifFlagFunction?.onTouchEvent(event) == true) {
            return true
        }
        if (clickRetryFunction?.onTouchEvent(event) == true) {
            return true
        }
        if (requestFunction.onTouchEvent(event)) {
            return true
        }
        if (recyclerCompatFunction.onTouchEvent(event)) {
            return true
        }
        if (clickPlayGifFunction?.onTouchEvent(event) == true) {
            return true
        }
        for (viewFunctionItem in viewFunctions) {
            if (viewFunctionItem.function.onTouchEvent(event)) {
                return true
            }
        }
        return false
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    fun onDrawableChanged(
        callPosition: String,
        oldDrawable: Drawable?,
        newDrawable: Drawable?
    ): Boolean {
        var needInvokeInvalidate = false
        needInvokeInvalidate = needInvokeInvalidate or requestFunction.onDrawableChanged(
            callPosition, oldDrawable, newDrawable
        )
        needInvokeInvalidate = needInvokeInvalidate or (showGifFlagFunction?.onDrawableChanged(
            callPosition, oldDrawable, newDrawable
        ) == true)
        needInvokeInvalidate = needInvokeInvalidate or (showImageFromFunction?.onDrawableChanged(
            callPosition, oldDrawable, newDrawable
        ) == true)
        needInvokeInvalidate = needInvokeInvalidate or (showPressedFunction?.onDrawableChanged(
            callPosition, oldDrawable, newDrawable
        ) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showDownloadProgressFunction?.onDrawableChanged(
                callPosition, oldDrawable, newDrawable
            ) == true)
        needInvokeInvalidate = needInvokeInvalidate or (clickRetryFunction?.onDrawableChanged(
            callPosition, oldDrawable, newDrawable
        ) == true)
        needInvokeInvalidate = needInvokeInvalidate or recyclerCompatFunction.onDrawableChanged(
            callPosition, oldDrawable, newDrawable
        )
        needInvokeInvalidate = needInvokeInvalidate or (clickPlayGifFunction?.onDrawableChanged(
            callPosition, oldDrawable, newDrawable
        ) == true)
        for (viewFunctionItem in viewFunctions) {
            needInvokeInvalidate =
                needInvokeInvalidate or viewFunctionItem.function.onDrawableChanged(
                    callPosition, oldDrawable, newDrawable
                )
        }
        return needInvokeInvalidate
    }

    /**
     * @return true：需要设置drawable为null
     */
    fun onDetachedFromWindow(): Boolean {
        var needSetImageNull = false
        needSetImageNull = needSetImageNull or requestFunction.onDetachedFromWindow()
        needSetImageNull = needSetImageNull or recyclerCompatFunction.onDetachedFromWindow()
        needSetImageNull = needSetImageNull or (showPressedFunction?.onDetachedFromWindow() == true)
        needSetImageNull =
            needSetImageNull or (showDownloadProgressFunction?.onDetachedFromWindow() == true)
        needSetImageNull = needSetImageNull or (showGifFlagFunction?.onDetachedFromWindow() == true)
        needSetImageNull =
            needSetImageNull or (showImageFromFunction?.onDetachedFromWindow() == true)
        needSetImageNull = needSetImageNull or (clickRetryFunction?.onDetachedFromWindow() == true)
        needSetImageNull =
            needSetImageNull or (clickPlayGifFunction?.onDetachedFromWindow() == true)
        for (viewFunctionItem in viewFunctions) {
            needSetImageNull = needSetImageNull or viewFunctionItem.function.onDetachedFromWindow()
        }
        return needSetImageNull
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    fun onReadyDisplay(uri: String): Boolean {
        var needInvokeInvalidate = false
        needInvokeInvalidate = needInvokeInvalidate or requestFunction.onReadyDisplay(uri)
        needInvokeInvalidate =
            needInvokeInvalidate or recyclerCompatFunction.onReadyDisplay(uri)
        needInvokeInvalidate =
            needInvokeInvalidate or (showPressedFunction?.onReadyDisplay(uri) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showDownloadProgressFunction?.onReadyDisplay(uri) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showGifFlagFunction?.onReadyDisplay(uri) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showImageFromFunction?.onReadyDisplay(uri) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickRetryFunction?.onReadyDisplay(uri) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickPlayGifFunction?.onReadyDisplay(uri) == true)
        for (viewFunctionItem in viewFunctions) {
            needInvokeInvalidate =
                needInvokeInvalidate or viewFunctionItem.function.onReadyDisplay(uri)
        }
        return needInvokeInvalidate
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    fun onDisplayStarted(): Boolean {
        var needInvokeInvalidate = false
        needInvokeInvalidate =
            needInvokeInvalidate or (showImageFromFunction?.onDisplayStarted() == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showDownloadProgressFunction?.onDisplayStarted() == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showGifFlagFunction?.onDisplayStarted() == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showPressedFunction?.onDisplayStarted() == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickRetryFunction?.onDisplayStarted() == true)
        needInvokeInvalidate = needInvokeInvalidate or requestFunction.onDisplayStarted()
        needInvokeInvalidate = needInvokeInvalidate or recyclerCompatFunction.onDisplayStarted()
        needInvokeInvalidate =
            needInvokeInvalidate or (clickPlayGifFunction?.onDisplayStarted() == true)
        for (viewFunctionItem in viewFunctions) {
            needInvokeInvalidate =
                needInvokeInvalidate or viewFunctionItem.function.onDisplayStarted()
        }
        return needInvokeInvalidate
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    fun onDisplayCompleted(
        drawable: Drawable,
        imageFrom: ImageFrom,
        imageAttrs: ImageAttrs
    ): Boolean {
        var needInvokeInvalidate = false
        needInvokeInvalidate =
            needInvokeInvalidate or (showImageFromFunction?.onDisplayCompleted(
                drawable, imageFrom, imageAttrs
            ) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showDownloadProgressFunction?.onDisplayCompleted(
                drawable, imageFrom, imageAttrs
            ) == true)
        needInvokeInvalidate = needInvokeInvalidate or (showGifFlagFunction?.onDisplayCompleted(
            drawable, imageFrom, imageAttrs
        ) == true)
        needInvokeInvalidate = needInvokeInvalidate or (showPressedFunction?.onDisplayCompleted(
            drawable, imageFrom, imageAttrs
        ) == true)
        needInvokeInvalidate = needInvokeInvalidate or (clickRetryFunction?.onDisplayCompleted(
            drawable, imageFrom, imageAttrs
        ) == true)
        needInvokeInvalidate = needInvokeInvalidate or requestFunction.onDisplayCompleted(
            drawable, imageFrom, imageAttrs
        )
        needInvokeInvalidate = needInvokeInvalidate or recyclerCompatFunction.onDisplayCompleted(
            drawable, imageFrom, imageAttrs
        )
        needInvokeInvalidate = needInvokeInvalidate or (clickPlayGifFunction?.onDisplayCompleted(
            drawable, imageFrom, imageAttrs
        ) == true)
        for (viewFunctionItem in viewFunctions) {
            needInvokeInvalidate =
                needInvokeInvalidate or viewFunctionItem.function.onDisplayCompleted(
                    drawable, imageFrom, imageAttrs
                )
        }
        return needInvokeInvalidate
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    fun onDisplayError(errorCause: ErrorCause): Boolean {
        var needInvokeInvalidate = false
        needInvokeInvalidate =
            needInvokeInvalidate or (showImageFromFunction?.onDisplayError(errorCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showDownloadProgressFunction?.onDisplayError(errorCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showGifFlagFunction?.onDisplayError(errorCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showPressedFunction?.onDisplayError(errorCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickRetryFunction?.onDisplayError(errorCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or requestFunction.onDisplayError(errorCause)
        needInvokeInvalidate =
            needInvokeInvalidate or recyclerCompatFunction.onDisplayError(errorCause)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickPlayGifFunction?.onDisplayError(errorCause) == true)
        for (viewFunctionItem in viewFunctions) {
            needInvokeInvalidate =
                needInvokeInvalidate or viewFunctionItem.function.onDisplayError(errorCause)
        }
        return needInvokeInvalidate
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    fun onDisplayCanceled(cancelCause: CancelCause): Boolean {
        var needInvokeInvalidate = false
        needInvokeInvalidate =
            needInvokeInvalidate or (showImageFromFunction?.onDisplayCanceled(cancelCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showDownloadProgressFunction?.onDisplayCanceled(cancelCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showGifFlagFunction?.onDisplayCanceled(cancelCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showPressedFunction?.onDisplayCanceled(cancelCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickRetryFunction?.onDisplayCanceled(cancelCause) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or requestFunction.onDisplayCanceled(cancelCause)
        needInvokeInvalidate =
            needInvokeInvalidate or recyclerCompatFunction.onDisplayCanceled(cancelCause)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickPlayGifFunction?.onDisplayCanceled(cancelCause) == true)
        for (viewFunctionItem in viewFunctions) {
            needInvokeInvalidate =
                needInvokeInvalidate or viewFunctionItem.function.onDisplayCanceled(cancelCause)
        }
        return needInvokeInvalidate
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    fun onUpdateDownloadProgress(totalLength: Int, completedLength: Int): Boolean {
        var needInvokeInvalidate = false
        needInvokeInvalidate =
            needInvokeInvalidate or (showImageFromFunction?.onUpdateDownloadProgress(
                totalLength,
                completedLength
            ) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showDownloadProgressFunction?.onUpdateDownloadProgress(
                totalLength,
                completedLength
            ) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showPressedFunction?.onUpdateDownloadProgress(
                totalLength,
                completedLength
            ) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (showGifFlagFunction?.onUpdateDownloadProgress(
                totalLength,
                completedLength
            ) == true)
        needInvokeInvalidate =
            needInvokeInvalidate or (clickRetryFunction?.onUpdateDownloadProgress(
                totalLength,
                completedLength
            ) == true)
        needInvokeInvalidate = needInvokeInvalidate or requestFunction.onUpdateDownloadProgress(
            totalLength,
            completedLength
        )
        needInvokeInvalidate =
            needInvokeInvalidate or recyclerCompatFunction.onUpdateDownloadProgress(
                totalLength,
                completedLength
            )
        needInvokeInvalidate =
            needInvokeInvalidate or (clickPlayGifFunction?.onUpdateDownloadProgress(
                totalLength,
                completedLength
            ) == true)
        for (viewFunctionItem in viewFunctions) {
            needInvokeInvalidate =
                needInvokeInvalidate or viewFunctionItem.function.onUpdateDownloadProgress(
                    totalLength,
                    completedLength
                )
        }
        return needInvokeInvalidate
    }
}