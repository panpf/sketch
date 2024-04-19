/*
 * Copyright 2023 Coil Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * ------------------------------------------------------------------------
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
package com.github.panpf.sketch.request.internal

import android.view.View
import androidx.annotation.MainThread
import androidx.core.view.ViewCompat
import com.github.panpf.sketch.core.R

internal val View.requestManager: ViewTargetRequestManager
    get() {
        val manager = getTag(R.id.sketch_request_manager) as ViewTargetRequestManager?
        if (manager != null) {
            return manager
        }
        return synchronized(this) {
            // Check again in case coil_request_manager was just set.
            (getTag(R.id.sketch_request_manager) as ViewTargetRequestManager?)
                ?: ViewTargetRequestManager(this).apply {
                    addOnAttachStateChangeListener(this)
                    setTag(R.id.sketch_request_manager, this)
                }
        }
    }

class ViewTargetRequestManager constructor(private val view: View) : BaseRequestManager(),
    View.OnAttachStateChangeListener {

    @MainThread
    override fun onViewAttachedToWindow(v: View) {
        // On Android the request may be executed before the View is attached to the Window
        // ViewTargetRequestDelegate.assertActive() will cancel the request when it detects
        //  that the View is not attached to the Window.
        // So the request must be restarted here
        restart()

//        callbackAttachedState()   // Because the request will always be restarted here, no callback is needed
    }

    @MainThread
    override fun onViewDetachedFromWindow(v: View) {
        currentRequestDelegate?.dispose()
        callbackAttachedState()
    }

    override fun isAttached(): Boolean {
        return ViewCompat.isAttachedToWindow(view)
    }
}