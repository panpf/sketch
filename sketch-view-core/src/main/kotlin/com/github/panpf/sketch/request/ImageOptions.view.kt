/*
 * Copyright (C) 2024 panpf <panpfpanpf@outlook.com>
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
import com.github.panpf.sketch.resize.internal.ViewSizeResolver


/**
 * Use the size of the View as the size of the resize
 *
 * @see com.github.panpf.sketch.view.core.test.request.ImageOptionsViewTest.testSizeWithView
 */
fun ImageOptions.Builder.sizeWithView(view: View, subtractPadding: Boolean = true): ImageOptions.Builder =
    apply {
        size(ViewSizeResolver(view, subtractPadding))
    }