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
package me.panpf.sketch.sample.util

import android.content.Context
import android.content.pm.PackageManager
import com.github.panpf.recycler.sticky.sample.bean.ListSeparator
import me.panpf.sketch.sample.bean.AppInfo
import java.util.*

class PinyinFlatAppsHelper(private val context: Context) {

    // Contains the following class types: PinyinGroup、AppInfo
    private val pinyinFlatAppList = loadInstalledAppList()

    private fun loadInstalledAppList(): List<Any> {
        val packageInfoList =
            context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        val appPackageList = packageInfoList.mapNotNull { packageInfo ->
            context.packageManager.getLaunchIntentForPackage(packageInfo.packageName)
                ?: return@mapNotNull null
            AppInfo.fromPackageInfo(context, packageInfo)!!
        }.sortedBy { it.namePinyinLowerCase }

        return ArrayList<Any>().apply {
            var lastPinyinFirstChar: Char? = null
            appPackageList.forEach { app ->
                val namePinyinFirstChar = app.namePinyin.first().uppercaseChar()
                if (lastPinyinFirstChar == null || namePinyinFirstChar != lastPinyinFirstChar) {
                    add(ListSeparator(namePinyinFirstChar.toString()))
                    lastPinyinFirstChar = namePinyinFirstChar
                }
                add(app)
            }
        }
    }

    val count = pinyinFlatAppList.size

    /**
     * Contains the following class types: PinyinGroup、AppInfo
     */
    fun getRange(fromIndex: Int, toIndexExclusive: Int): List<Any> {
        return pinyinFlatAppList.subList(fromIndex, toIndexExclusive)
    }

    /**
     * Contains the following class types: PinyinGroup、AppInfo
     */
    fun getAll() = pinyinFlatAppList
}