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

import android.graphics.drawable.Drawable
import com.github.panpf.sketch.decode.ImageAttrs
import com.github.panpf.sketch.request.CancelCause
import com.github.panpf.sketch.request.DisplayListener
import com.github.panpf.sketch.request.ErrorCause
import com.github.panpf.sketch.request.ImageFrom
import java.lang.ref.WeakReference

internal class DisplayListenerProxy(view: FunctionCallbackView) : DisplayListener {

    private val viewWeakReference: WeakReference<FunctionCallbackView> = WeakReference(view)

    override fun onStarted() {
        val view = viewWeakReference.get() ?: return
        val needInvokeInvalidate = view.functions.onDisplayStarted()
        if (needInvokeInvalidate) {
            view.invalidate()
        }
        view.wrappedDisplayListener?.onStarted()
    }

    override fun onCompleted(drawable: Drawable, imageFrom: ImageFrom, imageAttrs: ImageAttrs) {
        val view = viewWeakReference.get() ?: return
        val needInvokeInvalidate =
            view.functions.onDisplayCompleted(drawable, imageFrom, imageAttrs)
        if (needInvokeInvalidate) {
            view.invalidate()
        }
        view.wrappedDisplayListener?.onCompleted(drawable, imageFrom, imageAttrs)
    }

    override fun onError(cause: ErrorCause) {
        val view = viewWeakReference.get() ?: return
        val needInvokeInvalidate = view.functions.onDisplayError(cause)
        if (needInvokeInvalidate) {
            view.invalidate()
        }
        view.wrappedDisplayListener?.onError(cause)
    }

    override fun onCanceled(cause: CancelCause) {
        val view = viewWeakReference.get() ?: return
        val needInvokeInvalidate = view.functions.onDisplayCanceled(cause)
        if (needInvokeInvalidate) {
            view.invalidate()
        }
        view.wrappedDisplayListener?.onCanceled(cause)
    }
}