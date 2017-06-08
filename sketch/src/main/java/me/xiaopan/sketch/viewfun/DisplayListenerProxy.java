/*
 * Copyright (C) 2017 Peng fei Pan <sky@xiaopan.me>
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

package me.xiaopan.sketch.viewfun;

import android.graphics.drawable.Drawable;

import java.lang.ref.WeakReference;

import me.xiaopan.sketch.drawable.ImageAttrs;
import me.xiaopan.sketch.request.CancelCause;
import me.xiaopan.sketch.request.DisplayListener;
import me.xiaopan.sketch.request.ErrorCause;
import me.xiaopan.sketch.request.ImageFrom;

class DisplayListenerProxy implements DisplayListener {
    private WeakReference<FunctionCallbackView> viewWeakReference;

    public DisplayListenerProxy(FunctionCallbackView view) {
        this.viewWeakReference = new WeakReference<>(view);
    }

    @Override
    public void onStarted() {
        FunctionCallbackView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        boolean needInvokeInvalidate = view.getFunctions().onDisplayStarted();
        if (needInvokeInvalidate) {
            view.invalidate();
        }

        if (view.wrappedDisplayListener != null) {
            view.wrappedDisplayListener.onStarted();
        }
    }

    @Override
    public void onCompleted(Drawable drawable, ImageFrom imageFrom, ImageAttrs imageAttrs) {
        FunctionCallbackView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        boolean needInvokeInvalidate = view.getFunctions().onDisplayCompleted(drawable, imageFrom, imageAttrs);
        if (needInvokeInvalidate) {
            view.invalidate();
        }

        if (view.wrappedDisplayListener != null) {
            view.wrappedDisplayListener.onCompleted(drawable, imageFrom, imageAttrs);
        }
    }

    @Override
    public void onError(ErrorCause errorCause) {
        FunctionCallbackView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        boolean needInvokeInvalidate = view.getFunctions().onDisplayError(errorCause);
        if (needInvokeInvalidate) {
            view.invalidate();
        }

        if (view.wrappedDisplayListener != null) {
            view.wrappedDisplayListener.onError(errorCause);
        }
    }

    @Override
    public void onCanceled(CancelCause cancelCause) {
        FunctionCallbackView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        boolean needInvokeInvalidate = view.getFunctions().onDisplayCanceled(cancelCause);
        if (needInvokeInvalidate) {
            view.invalidate();
        }

        if (view.wrappedDisplayListener != null) {
            view.wrappedDisplayListener.onCanceled(cancelCause);
        }
    }
}
