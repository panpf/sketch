/*
 * Copyright (C) 2022 panpf <panpfpanpf@outlook.com>
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
package com.github.panpf.sketch.request

import android.view.View
import androidx.lifecycle.DefaultLifecycleObserver
import com.github.panpf.sketch.request.internal.requestManager
import kotlinx.coroutines.Deferred
import java.lang.ref.WeakReference

/**
 * A disposable for requests that are attached to a [View].
 *
 * [com.github.panpf.sketch.target.ViewTarget] requests are automatically cancelled in when the view is detached
 * and are restarted when the view is attached.
 *
 * [isDisposed] only returns 'true' when this disposable's request is cleared (due to
 * [DefaultLifecycleObserver.onDestroy]) or replaced by a new request attached to the view.
 */
class ViewTargetDisposable(
    private val viewReference: WeakReference<View>,
    @Volatile override var job: Deferred<ImageResult>
) : Disposable {

    private val view: View?
        get() = viewReference.get()

    override val isDisposed: Boolean
        get() = view?.requestManager?.isDisposed(this) != false

    override fun dispose() {
        if (!isDisposed) {
            view?.requestManager?.dispose()
        }
    }
}