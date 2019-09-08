/*
 * Copyright (C) 2019 Peng fei Pan <panpfpanpf@outlook.me>
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

package me.panpf.sketch.viewfun;

import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;

class ViewFunctions {
    @NonNull
    RequestFunction requestFunction;
    @Nullable
    ShowImageFromFunction showImageFromFunction;
    @Nullable
    ShowDownloadProgressFunction showDownloadProgressFunction;
    @Nullable
    ShowPressedFunction showPressedFunction;
    @Nullable
    ShowGifFlagFunction showGifFlagFunction;
    @Nullable
    ClickRetryFunction clickRetryFunction;
    @Nullable
    ClickPlayGifFunction clickPlayGifFunction;
    @NonNull
    private RecyclerCompatFunction recyclerCompatFunction;
    private List<ViewFunctionItem> viewFunctions = new LinkedList<>();

    ViewFunctions(FunctionCallbackView view) {
        requestFunction = new RequestFunction(view);
        recyclerCompatFunction = new RecyclerCompatFunction(view);
    }

    public void addViewFunction(@NonNull ViewFunction viewFunction, int priority) {
        viewFunctions.add(new ViewFunctionItem(priority, viewFunction));
        Collections.sort(viewFunctions, new Comparator<ViewFunctionItem>() {
            @Override
            public int compare(ViewFunctionItem o1, ViewFunctionItem o2) {
                return -1 * (o1.priority - o2.priority);
            }
        });
    }

    public void removeViewFunction(@NonNull ViewFunction viewFunction) {
        Iterator<ViewFunctionItem> iterator = viewFunctions.iterator();
        while (iterator.hasNext()) {
            ViewFunctionItem functionItem = iterator.next();
            if (functionItem.function.equals(viewFunction)) {
                iterator.remove();
            }
        }
    }

    void onAttachedToWindow() {
        requestFunction.onAttachedToWindow();
        if (recyclerCompatFunction != null) {
            recyclerCompatFunction.onAttachedToWindow();
        }
        if (showPressedFunction != null) {
            showPressedFunction.onAttachedToWindow();
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction.onAttachedToWindow();
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction.onAttachedToWindow();
        }
        if (showImageFromFunction != null) {
            showImageFromFunction.onAttachedToWindow();
        }
        if (clickRetryFunction != null) {
            clickRetryFunction.onAttachedToWindow();
        }
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction.onAttachedToWindow();
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            viewFunctionItem.function.onAttachedToWindow();
        }
    }

    void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (showImageFromFunction != null) {
            showImageFromFunction.onLayout(changed, left, top, right, bottom);
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction.onLayout(changed, left, top, right, bottom);
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction.onLayout(changed, left, top, right, bottom);
        }
        if (showPressedFunction != null) {
            showPressedFunction.onLayout(changed, left, top, right, bottom);
        }
        if (clickRetryFunction != null) {
            clickRetryFunction.onLayout(changed, left, top, right, bottom);
        }
        if (requestFunction != null) {
            requestFunction.onLayout(changed, left, top, right, bottom);
        }
        if (recyclerCompatFunction != null) {
            recyclerCompatFunction.onLayout(changed, left, top, right, bottom);
        }
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction.onLayout(changed, left, top, right, bottom);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            viewFunctionItem.function.onLayout(changed, left, top, right, bottom);
        }
    }

    void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (requestFunction != null) {
            requestFunction.onSizeChanged(w, h, oldw, oldh);
        }
        if (recyclerCompatFunction != null) {
            recyclerCompatFunction.onSizeChanged(w, h, oldw, oldh);
        }
        if (showPressedFunction != null) {
            showPressedFunction.onSizeChanged(w, h, oldw, oldh);
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction.onSizeChanged(w, h, oldw, oldh);
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction.onSizeChanged(w, h, oldw, oldh);
        }
        if (showImageFromFunction != null) {
            showImageFromFunction.onSizeChanged(w, h, oldw, oldh);
        }
        if (clickRetryFunction != null) {
            clickRetryFunction.onSizeChanged(w, h, oldw, oldh);
        }
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction.onSizeChanged(w, h, oldw, oldh);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            viewFunctionItem.function.onSizeChanged(w, h, oldw, oldh);
        }
    }

    void onDraw(Canvas canvas) {
        if (showPressedFunction != null) {
            showPressedFunction.onDraw(canvas);
        }
        if (showDownloadProgressFunction != null) {
            showDownloadProgressFunction.onDraw(canvas);
        }
        if (showImageFromFunction != null) {
            showImageFromFunction.onDraw(canvas);
        }
        if (showGifFlagFunction != null) {
            showGifFlagFunction.onDraw(canvas);
        }
        if (clickRetryFunction != null) {
            clickRetryFunction.onDraw(canvas);
        }
        if (requestFunction != null) {
            requestFunction.onDraw(canvas);
        }
        if (recyclerCompatFunction != null) {
            recyclerCompatFunction.onDraw(canvas);
        }
        if (clickPlayGifFunction != null) {
            clickPlayGifFunction.onDraw(canvas);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            viewFunctionItem.function.onDraw(canvas);
        }
    }

    /**
     * @return true：事件已处理
     */
    boolean onTouchEvent(MotionEvent event) {
        if (showPressedFunction != null && showPressedFunction.onTouchEvent(event)) {
            return true;
        }
        if (showDownloadProgressFunction != null && showDownloadProgressFunction.onTouchEvent(event)) {
            return true;
        }
        if (showImageFromFunction != null && showImageFromFunction.onTouchEvent(event)) {
            return true;
        }
        if (showGifFlagFunction != null && showGifFlagFunction.onTouchEvent(event)) {
            return true;
        }
        if (clickRetryFunction != null && clickRetryFunction.onTouchEvent(event)) {
            return true;
        }
        if (requestFunction != null && requestFunction.onTouchEvent(event)) {
            return true;
        }
        if (recyclerCompatFunction != null && recyclerCompatFunction.onTouchEvent(event)) {
            return true;
        }
        if (clickPlayGifFunction != null && clickPlayGifFunction.onTouchEvent(event)) {
            return true;
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            if (viewFunctionItem.function.onTouchEvent(event)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDrawableChanged(String callPosition, Drawable oldDrawable, Drawable newDrawable) {
        boolean needInvokeInvalidate = false;

        if (requestFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= requestFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        if (showGifFlagFunction != null) {
            needInvokeInvalidate |= showGifFlagFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        if (showImageFromFunction != null) {
            needInvokeInvalidate |= showImageFromFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        if (showPressedFunction != null) {
            needInvokeInvalidate |= showPressedFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        if (showDownloadProgressFunction != null) {
            needInvokeInvalidate |= showDownloadProgressFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        if (clickRetryFunction != null) {
            needInvokeInvalidate |= clickRetryFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        if (recyclerCompatFunction != null) {
            needInvokeInvalidate |= recyclerCompatFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        if (clickPlayGifFunction != null) {
            needInvokeInvalidate |= clickPlayGifFunction.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needInvokeInvalidate |= viewFunctionItem.function.onDrawableChanged(callPosition, oldDrawable, newDrawable);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要设置drawable为null
     */
    boolean onDetachedFromWindow() {
        boolean needSetImageNull = false;

        if (requestFunction != null) {
            //noinspection ConstantConditions
            needSetImageNull |= requestFunction.onDetachedFromWindow();
        }
        if (recyclerCompatFunction != null) {
            needSetImageNull |= recyclerCompatFunction.onDetachedFromWindow();
        }
        if (showPressedFunction != null) {
            needSetImageNull |= showPressedFunction.onDetachedFromWindow();
        }
        if (showDownloadProgressFunction != null) {
            needSetImageNull |= showDownloadProgressFunction.onDetachedFromWindow();
        }
        if (showGifFlagFunction != null) {
            needSetImageNull |= showGifFlagFunction.onDetachedFromWindow();
        }
        if (showImageFromFunction != null) {
            needSetImageNull |= showImageFromFunction.onDetachedFromWindow();
        }
        if (clickRetryFunction != null) {
            needSetImageNull |= clickRetryFunction.onDetachedFromWindow();
        }
        if (clickPlayGifFunction != null) {
            needSetImageNull |= clickPlayGifFunction.onDetachedFromWindow();
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needSetImageNull |= viewFunctionItem.function.onDetachedFromWindow();
        }

        return needSetImageNull;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onReadyDisplay(@NonNull String uri) {
        boolean needInvokeInvalidate = false;

        if (requestFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= requestFunction.onReadyDisplay(uri);
        }
        if (recyclerCompatFunction != null) {
            needInvokeInvalidate |= recyclerCompatFunction.onReadyDisplay(uri);
        }
        if (showPressedFunction != null) {
            needInvokeInvalidate |= showPressedFunction.onReadyDisplay(uri);
        }
        if (showDownloadProgressFunction != null) {
            needInvokeInvalidate |= showDownloadProgressFunction.onReadyDisplay(uri);
        }
        if (showGifFlagFunction != null) {
            needInvokeInvalidate |= showGifFlagFunction.onReadyDisplay(uri);
        }
        if (showImageFromFunction != null) {
            needInvokeInvalidate |= showImageFromFunction.onReadyDisplay(uri);
        }
        if (clickRetryFunction != null) {
            needInvokeInvalidate |= clickRetryFunction.onReadyDisplay(uri);
        }
        if (clickPlayGifFunction != null) {
            needInvokeInvalidate |= clickPlayGifFunction.onReadyDisplay(uri);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needInvokeInvalidate |= viewFunctionItem.function.onReadyDisplay(uri);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayStarted() {
        boolean needInvokeInvalidate = false;

        if (showImageFromFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= showImageFromFunction.onDisplayStarted();
        }
        if (showDownloadProgressFunction != null) {
            needInvokeInvalidate |= showDownloadProgressFunction.onDisplayStarted();
        }
        if (showGifFlagFunction != null) {
            needInvokeInvalidate |= showGifFlagFunction.onDisplayStarted();
        }
        if (showPressedFunction != null) {
            needInvokeInvalidate |= showPressedFunction.onDisplayStarted();
        }
        if (clickRetryFunction != null) {
            needInvokeInvalidate |= clickRetryFunction.onDisplayStarted();
        }
        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayStarted();
        }
        if (recyclerCompatFunction != null) {
            needInvokeInvalidate |= recyclerCompatFunction.onDisplayStarted();
        }
        if (clickPlayGifFunction != null) {
            needInvokeInvalidate |= clickPlayGifFunction.onDisplayStarted();
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needInvokeInvalidate |= viewFunctionItem.function.onDisplayStarted();
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
        boolean needInvokeInvalidate = false;

        if (showImageFromFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= showImageFromFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        if (showDownloadProgressFunction != null) {
            needInvokeInvalidate |= showDownloadProgressFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        if (showGifFlagFunction != null) {
            needInvokeInvalidate |= showGifFlagFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        if (showPressedFunction != null) {
            needInvokeInvalidate |= showPressedFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        if (clickRetryFunction != null) {
            needInvokeInvalidate |= clickRetryFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        if (recyclerCompatFunction != null) {
            needInvokeInvalidate |= recyclerCompatFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        if (clickPlayGifFunction != null) {
            needInvokeInvalidate |= clickPlayGifFunction.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needInvokeInvalidate |= viewFunctionItem.function.onDisplayCompleted(drawable, imageFrom, imageAttrs);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayError(@NonNull ErrorCause errorCause) {
        boolean needInvokeInvalidate = false;

        if (showImageFromFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= showImageFromFunction.onDisplayError(errorCause);
        }
        if (showDownloadProgressFunction != null) {
            needInvokeInvalidate |= showDownloadProgressFunction.onDisplayError(errorCause);
        }
        if (showGifFlagFunction != null) {
            needInvokeInvalidate |= showGifFlagFunction.onDisplayError(errorCause);
        }
        if (showPressedFunction != null) {
            needInvokeInvalidate |= showPressedFunction.onDisplayError(errorCause);
        }
        if (clickRetryFunction != null) {
            needInvokeInvalidate |= clickRetryFunction.onDisplayError(errorCause);
        }
        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayError(errorCause);
        }
        if (recyclerCompatFunction != null) {
            needInvokeInvalidate |= recyclerCompatFunction.onDisplayError(errorCause);
        }
        if (clickPlayGifFunction != null) {
            needInvokeInvalidate |= clickPlayGifFunction.onDisplayError(errorCause);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needInvokeInvalidate |= viewFunctionItem.function.onDisplayError(errorCause);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onDisplayCanceled(@NonNull CancelCause cancelCause) {
        boolean needInvokeInvalidate = false;

        if (showImageFromFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= showImageFromFunction.onDisplayCanceled(cancelCause);
        }
        if (showDownloadProgressFunction != null) {
            needInvokeInvalidate |= showDownloadProgressFunction.onDisplayCanceled(cancelCause);
        }
        if (showGifFlagFunction != null) {
            needInvokeInvalidate |= showGifFlagFunction.onDisplayCanceled(cancelCause);
        }
        if (showPressedFunction != null) {
            needInvokeInvalidate |= showPressedFunction.onDisplayCanceled(cancelCause);
        }
        if (clickRetryFunction != null) {
            needInvokeInvalidate |= clickRetryFunction.onDisplayCanceled(cancelCause);
        }
        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onDisplayCanceled(cancelCause);
        }
        if (recyclerCompatFunction != null) {
            needInvokeInvalidate |= recyclerCompatFunction.onDisplayCanceled(cancelCause);
        }
        if (clickPlayGifFunction != null) {
            needInvokeInvalidate |= clickPlayGifFunction.onDisplayCanceled(cancelCause);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needInvokeInvalidate |= viewFunctionItem.function.onDisplayCanceled(cancelCause);
        }

        return needInvokeInvalidate;
    }

    /**
     * @return true：需要调用invalidate()刷新view
     */
    boolean onUpdateDownloadProgress(int totalLength, int completedLength) {
        boolean needInvokeInvalidate = false;

        if (showImageFromFunction != null) {
            //noinspection ConstantConditions
            needInvokeInvalidate |= showImageFromFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        if (showDownloadProgressFunction != null) {
            needInvokeInvalidate |= showDownloadProgressFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        if (showPressedFunction != null) {
            needInvokeInvalidate |= showPressedFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        if (showGifFlagFunction != null) {
            needInvokeInvalidate |= showGifFlagFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        if (clickRetryFunction != null) {
            needInvokeInvalidate |= clickRetryFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        if (requestFunction != null) {
            needInvokeInvalidate |= requestFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        if (recyclerCompatFunction != null) {
            needInvokeInvalidate |= recyclerCompatFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        if (clickPlayGifFunction != null) {
            needInvokeInvalidate |= clickPlayGifFunction.onUpdateDownloadProgress(totalLength, completedLength);
        }
        for (ViewFunctionItem viewFunctionItem : viewFunctions) {
            needInvokeInvalidate |= viewFunctionItem.function.onUpdateDownloadProgress(totalLength, completedLength);
        }

        return needInvokeInvalidate;
    }
}
