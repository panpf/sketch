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

import com.github.panpf.sketch.Sketch
import com.github.panpf.sketch.fetch.FetchResult
import com.github.panpf.sketch.fetch.Fetcher
import com.github.panpf.sketch.fetch.HttpUriFetcher
import com.github.panpf.sketch.request.ImageRequest
import com.github.panpf.sketch.source.AssetDataSource

class TestHttpUriFetcher(sketch: Sketch, request: ImageRequest, url: String) :
    HttpUriFetcher(sketch, request, url) {

    override suspend fun fetch(): Result<FetchResult> {
        return Result.success(
            FetchResult(
                AssetDataSource(sketch, request, "fake_asset_name"),
                null
            )
        )
    }

    class Factory : Fetcher.Factory {

        override fun create(sketch: Sketch, request: ImageRequest): HttpUriFetcher? {
            val uri = request.uri
            val scheme = uri.scheme
            return if (
                SCHEME.equals(scheme, ignoreCase = true)
                || SCHEME_HTTPS.equals(scheme, ignoreCase = true)
            ) {
                TestHttpUriFetcher(sketch, request, request.uri.toString())
            } else {
                null
            }
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            return other is Factory
        }

        override fun hashCode(): Int {
            return this@Factory::class.hashCode()
        }

        override fun toString(): String = "TestHttpUriFetcher.Factory"
    }
}