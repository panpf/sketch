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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper
import com.github.panpf.sketch.sample.util.ExifOrientationTestFileHelper.TestFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExifOrientationTestPagerViewModel(application1: Application) :
    LifecycleAndroidViewModel(application1) {

    val data = MutableLiveData<List<TestFile>>()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            data.postValue(ExifOrientationTestFileHelper(application1, "sample.jpeg").files())
        }
    }
}