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
import android.content.pm.PackageInfo
import android.os.Parcelable
import android.text.format.Formatter
import com.github.panpf.assemblyadapter.recycler.DiffKey
import com.github.promeg.pinyinhelper.Pinyin
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.*

@Parcelize
data class AppInfo constructor(
    val packageName: String,
    val name: String,
    val namePinyin: String,
    val versionName: String,
    val versionCode: Int,
    val apkFilePath: String,
    val apkSize: Long,
    val formattedAppSize: String,
    val systemApp: Boolean,
) : DiffKey, Parcelable {

    @IgnoredOnParcel
    override val diffKey: String = packageName

    @IgnoredOnParcel
    val namePinyinLowerCase by lazy { namePinyin.lowercase(Locale.getDefault()) }

    companion object {
        fun fromPackageInfo(context: Context, packageInfo: PackageInfo): AppInfo? {
            context.packageManager.getLaunchIntentForPackage(packageInfo.packageName) ?: return null
            val name =
                packageInfo.applicationInfo.loadLabel(context.packageManager).toString()
            val apkSize = File(packageInfo.applicationInfo.publicSourceDir).length()
            return AppInfo(
                packageName = packageInfo.packageName,
                name = name,
                namePinyin = Pinyin.toPinyin(name, ""),
                versionName = packageInfo.versionName.orEmpty(),
                versionCode = packageInfo.versionCode,
                apkFilePath = packageInfo.applicationInfo.publicSourceDir,
                apkSize = apkSize,
                formattedAppSize = Formatter.formatFileSize(context, apkSize),
                packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0,
            )
        }
    }
}
