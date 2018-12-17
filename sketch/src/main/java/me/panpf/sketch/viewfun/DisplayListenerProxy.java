/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;

import me.panpf.sketch.decode.ImageAttrs;
import me.panpf.sketch.request.CancelCause;
import me.panpf.sketch.request.DisplayListener;
import me.panpf.sketch.request.ErrorCause;
import me.panpf.sketch.request.ImageFrom;

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
    public void onCompleted(@NonNull Drawable drawable, @NonNull ImageFrom imageFrom, @NonNull ImageAttrs imageAttrs) {
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
    public void onError(@NonNull ErrorCause cause) {
        FunctionCallbackView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        boolean needInvokeInvalidate = view.getFunctions().onDisplayError(cause);
        if (needInvokeInvalidate) {
            view.invalidate();
        }

        if (view.wrappedDisplayListener != null) {
            view.wrappedDisplayListener.onError(cause);
        }
    }

    @Override
    public void onCanceled(@NonNull CancelCause cause) {
        FunctionCallbackView view = viewWeakReference.get();
        if (view == null) {
            return;
        }

        boolean needInvokeInvalidate = view.getFunctions().onDisplayCanceled(cause);
        if (needInvokeInvalidate) {
            view.invalidate();
        }

        if (view.wrappedDisplayListener != null) {
            view.wrappedDisplayListener.onCanceled(cause);
        }
    }
}
