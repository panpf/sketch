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

package com.github.panpf.sketch.ability

import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import com.github.panpf.sketch.request.PauseLoadWhenScrollingRequestInterceptor

/**
 * Bind [PauseLoadWhenScrollingRequestInterceptor] to [ScrollableState], so that when the user scrolls, the image loading will be paused
 *
 * @see com.github.panpf.sketch.extensions.compose.common.test.ability.PauseLoadWhenScrollingTest.testBindPauseLoadWhenScrolling
 */
@Composable
fun bindPauseLoadWhenScrolling(scrollableState: ScrollableState) {
    LaunchedEffect(scrollableState) {
        snapshotFlow { scrollableState.isScrollInProgress }.collect {
            PauseLoadWhenScrollingRequestInterceptor.scrolling = it
        }
    }
}