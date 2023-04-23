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

import com.github.panpf.sketch.request.DownloadData
import com.github.panpf.sketch.target.DownloadTarget
import com.github.panpf.sketch.util.SketchException

class TestDownloadTarget : DownloadTarget {

    var start: String? = null
    var downloadData: DownloadData? = null
    var exception: SketchException? = null

    override fun onStart() {
        super.onStart()
        this.start = "onStart"
    }

    override fun onSuccess(result: DownloadData) {
        super.onSuccess(result)
        this.downloadData = result
    }

    override fun onError(exception: SketchException) {
        super.onError(exception)
        this.exception = exception
    }
}