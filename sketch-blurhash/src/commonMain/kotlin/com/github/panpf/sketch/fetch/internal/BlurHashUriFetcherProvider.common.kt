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

package com.github.panpf.sketch.fetch.internal

import com.github.panpf.sketch.PlatformContext
//import com.github.panpf.sketch.fetch.AppIconUriFetcher
import com.github.panpf.sketch.fetch.BlurHashUriFetcher
import com.github.panpf.sketch.util.FetcherProvider

///**
// * Cooperate with [ComponentLoader] to achieve automatic registration [ComposeResourceUriFetcher]
// *
// * @see com.github.panpf.sketch.compose.resources.jvm.test.fetch.internal.ComposeResourceUriFetcherProviderJvmTest
// * @see com.github.panpf.sketch.compose.resources.nonjvm.test.fetch.internal.ComposeResourceUriFetcherProviderNonJvmTest
// */

expect class BlurHashUriFetcherProvider : FetcherProvider {

    override fun factory(context: PlatformContext): BlurHashUriFetcher.Factory
}
