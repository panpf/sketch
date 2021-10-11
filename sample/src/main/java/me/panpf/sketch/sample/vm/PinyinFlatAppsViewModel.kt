/*
 * Copyright (C) 2021 panpf <panpfpanpf@oulook.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.panpf.sketch.sample.bean.AppsOverview
import me.panpf.sketch.sample.util.PinyinFlatAppsHelper

class PinyinFlatAppsViewModel(application: Application) : AndroidViewModel(application) {

    val pinyinFlatAppListData = MutableLiveData<List<Any>>()
    val loadingData = MutableLiveData<Boolean>()

    val appsOverviewData = MutableLiveData<AppsOverview>()

    init {
        refresh()
    }

    fun refresh() {
        refreshAppsOverview()
        refreshAppList()
    }

    private fun refreshAppList() {
        viewModelScope.launch {
            loadingData.postValue(true)
            val list = withContext(Dispatchers.IO) {
                PinyinFlatAppsHelper(getApplication()).getAll()
            }
            pinyinFlatAppListData.postValue(list)
            loadingData.postValue(false)
        }
    }

    private fun refreshAppsOverview() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                appsOverviewData.postValue(AppsOverview.build(getApplication()))
            }
        }
    }
}