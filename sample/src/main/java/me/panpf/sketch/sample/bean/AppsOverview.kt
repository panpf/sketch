/*
 * Copyright (C) 2021 panpf <panpfpanpf@outlook.com>
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
package me.panpf.sketch.sample.bean

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Parcelable
import com.github.panpf.assemblyadapter.recycler.DiffKey
import com.github.promeg.pinyinhelper.Pinyin
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class AppsOverview(val count: Int, val userAppCount: Int, val groupCount: Int) :
    Parcelable,
    DiffKey {

    @IgnoredOnParcel
    override val diffKey: String = "AppsOverview"

    companion object {

        fun build(context: Context): AppsOverview {
            var count = 0
            var userAppCount = 0
            val packageInfoList =
                context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
            val pinyinGroupCount = packageInfoList.mapNotNull { packageInfo ->
                context.packageManager.getLaunchIntentForPackage(packageInfo.packageName)
                    ?: return@mapNotNull null
                count++
                if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    userAppCount++
                }
                val name =
                    packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
                val namePinyinLowercase =
                    Pinyin.toPinyin(name, "").lowercase(Locale.getDefault())
                namePinyinLowercase.first()
            }.distinct().count()
            return AppsOverview(count, userAppCount, pinyinGroupCount)
        }
    }
}