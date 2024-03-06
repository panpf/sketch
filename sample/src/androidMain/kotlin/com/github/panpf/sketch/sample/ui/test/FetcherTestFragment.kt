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
package com.github.panpf.sketch.sample.ui.test

import android.app.Application
import android.content.ContentUris
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Images.Media
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle.State
import androidx.lifecycle.viewModelScope
import com.github.panpf.assemblyadapter.pager2.AssemblyFragmentStateAdapter
import com.github.panpf.sketch.PlatformContext
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.resources.AssetImages
import com.github.panpf.sketch.sample.databinding.FragmentTabPagerBinding
import com.github.panpf.sketch.sample.ui.base.BaseToolbarBindingFragment
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.ui.model.ImageDetail
import com.github.panpf.sketch.sample.util.repeatCollectWithLifecycle
import com.github.panpf.sketch.sample.util.versionCodeCompat
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FetcherTestFragment : BaseToolbarBindingFragment<FragmentTabPagerBinding>() {

    private val viewModel by viewModels<FetcherTestViewModel>()

    override fun onViewCreated(
        toolbar: Toolbar,
        binding: FragmentTabPagerBinding,
        savedInstanceState: Bundle?
    ) {
        toolbar.title = "Fetcher"

        viewModel.data.repeatCollectWithLifecycle(viewLifecycleOwner, State.STARTED) { data ->
            val imageFromData = data ?: return@repeatCollectWithLifecycle
            val images = imageFromData.map { it.imageUri }.map { s ->
                ImageDetail(s, s, s)
            }

            binding.pager.adapter = AssemblyFragmentStateAdapter(
                fragment = this,
                itemFactoryList = listOf(FetcherTestImageFragment.ItemFactory()),
                initDataList = images
            )

            TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
                tab.text = imageFromData[position].title
            }.attach()
        }
    }

    class FetcherTestViewModel(
        application1: Application
    ) : LifecycleAndroidViewModel(application1) {

        private val _data = MutableStateFlow<List<FetcherTestItem>?>(null)
        val data: StateFlow<List<FetcherTestItem>?> = _data

        init {
            viewModelScope.launch {
                _data.value = buildFetcherTestItems(application1)
            }
        }
    }
}

actual suspend fun buildFetcherTestItems(context: PlatformContext): List<FetcherTestItem> {
    val localFirstPhotoPath = loadLocalFirstPhotoPath(context)
    val localSecondPhotoUri = loadLocalSecondPhotoUri(context)
    val headerUserPackageInfo = loadUserAppPackageInfo(context, true)
    return buildList {
        add(FetcherTestItem(title = "HTTP", AssetImages.HTTP))
        add(FetcherTestItem(title = "HTTPS", AssetImages.HTTPS))
        if (localSecondPhotoUri != null) {
            add(FetcherTestItem(title = "CONTENT", localSecondPhotoUri.toString()))
        }
        if (localFirstPhotoPath != null) {
            add(FetcherTestItem(title = "FILE", localFirstPhotoPath))
        }
        add(FetcherTestItem(title = "ASSET", AssetImages.statics.first().uri))
        val resourceImageUri = newResourceUri(com.github.panpf.sketch.sample.R.mipmap.ic_launcher)
        add(FetcherTestItem(title = "RES_ID", resourceImageUri))
        add(FetcherTestItem(title = "RES_NAME", newResourceUri("drawable", "bg_circle_accent")))
        val appIconUri = newAppIconUri(
            headerUserPackageInfo.packageName,
            headerUserPackageInfo.versionCodeCompat
        )
        add(FetcherTestItem(title = "APP_ICON", appIconUri))
        add(FetcherTestItem(title = "BASE64", AssetImages.BASE64_IMAGE))
    }
}

private suspend fun loadUserAppPackageInfo(
    context: PlatformContext,
    fromHeader: Boolean
): PackageInfo {
    return withContext(Dispatchers.IO) {
        val packageList =
            context.packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS)
        (if (fromHeader) {
            packageList.find {
                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } else {
            packageList.findLast {
                it.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0
            }
        } ?: context.packageManager.getPackageInfo(context.packageName, 0))
    }
}

private suspend fun loadLocalFirstPhotoPath(context: PlatformContext): String? {
    return withContext(Dispatchers.IO) {
        val cursor = context.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(Media.DATA),
            null,
            null,
            Media.DATE_TAKEN + " DESC" + " limit " + 0 + "," + 1
        )
        var imagePath: String? = null
        cursor?.use {
            if (cursor.moveToNext()) {
                imagePath =
                    cursor.getString(cursor.getColumnIndexOrThrow(Media.DATA))
            }
        }
        imagePath
    }
}

private suspend fun loadLocalSecondPhotoUri(context: PlatformContext): Uri? {
    return withContext(Dispatchers.IO) {
        val cursor = context.contentResolver.query(
            Media.EXTERNAL_CONTENT_URI,
            arrayOf(Media._ID),
            null,
            null,
            Media.DATE_TAKEN + " DESC" + " limit " + 1 + "," + 1
        )
        var imageId: Long? = null
        cursor?.use {
            if (cursor.moveToNext()) {
                imageId =
                    cursor.getLong(cursor.getColumnIndexOrThrow(Media._ID))
            }
        }
        if (imageId != null) {
            ContentUris.withAppendedId(Media.EXTERNAL_CONTENT_URI, imageId!!)
        } else {
            null
        }
    }
}