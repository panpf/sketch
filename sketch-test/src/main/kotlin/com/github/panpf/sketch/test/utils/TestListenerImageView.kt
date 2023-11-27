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
package com.github.panpf.sketch.test.utils

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import com.github.panpf.sketch.request.DisplayListenerProvider
import com.github.panpf.sketch.request.DisplayRequest
import com.github.panpf.sketch.request.DisplayResult.Error
import com.github.panpf.sketch.request.DisplayResult.Success
import com.github.panpf.sketch.request.Listener
import com.github.panpf.sketch.request.ProgressListener

class TestListenerImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ImageView(context, attrs), DisplayListenerProvider {

    override fun getDisplayListener(): Listener<DisplayRequest, Success, Error> {
        return object : Listener<DisplayRequest, Success, Error> {

        }
    }

    override fun getDisplayProgressListener(): ProgressListener<DisplayRequest> {
        return ProgressListener { _, _, _ -> }
    }
}