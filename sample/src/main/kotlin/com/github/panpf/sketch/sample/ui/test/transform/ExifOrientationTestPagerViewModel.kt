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
package com.github.panpf.sketch.sample.ui.test.transform

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper.TestFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ExifOrientationTestPagerViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    private val _data = MutableStateFlow<List<TestFile>>(emptyList())
    val data: StateFlow<List<TestFile>> = _data

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _data.value =
                ExifOrientationTestFileHelper(application1, AssetImages.jpeg.fileName).files()
        }
    }
}