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
package com.github.panpf.sketch.sample.ui.test.format

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.util.versionCodeCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecoderTestViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    private val _data = MutableStateFlow<Pair<Array<String>, List<ImageDetail>>?>(null)
    val data: StateFlow<Pair<Array<String>, List<ImageDetail>>?> = _data

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val headerUserPackageInfo = loadUserAppPackageInfo(true)
            val footerUserPackageInfo = loadUserAppPackageInfo(false)

            val imageDetails = AssetImages.STATICS.plus(AssetImages.ANIMATEDS).plus(
                arrayOf(
                    newResourceUri(drawable.bg_circle_accent),
                    newResourceUri(drawable.ic_play),
                    footerUserPackageInfo.applicationInfo.publicSourceDir,
                    newAppIconUri(
                        headerUserPackageInfo.packageName,
                        headerUserPackageInfo.versionCodeCompat
                    )
                )
            ).mapIndexed { index, s ->
                ImageDetail(index, s, s, s)
            }
            val titles = AssetImages.STATICS.plus(AssetImages.ANIMATEDS).map { uri ->
                uri.substring(uri.lastIndexOf(".") + 1).uppercase().let { suffix ->
                    if (uri.endsWith("anim.webp") || uri.endsWith("anim.heif")) {
                        suffix + "_ANIM"
                    } else {
                        suffix
                    }
                }
            }.plus(
                listOf(
                    "XML",
                    "VECTOR",
                    "APK_ICON",
                    "APP_ICON",
                )
            ).toTypedArray()
            _data.value = titles to imageDetails
        }
    }

    private suspend fun loadUserAppPackageInfo(fromHeader: Boolean): PackageInfo {
        return withContext(Dispatchers.IO) {
            val packageList =
                application1.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            (if (fromHeader) {
                packageList.find {
                    it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
                }
            } else {
                packageList.findLast {
                    it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
                }
            } ?: application1.packageManager.getPackageInfo(application1.packageName, 0))
        }
    }
}