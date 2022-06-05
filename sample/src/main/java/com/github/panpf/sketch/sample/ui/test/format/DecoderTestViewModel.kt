package com.github.panpf.sketch.sample.ui.test.format

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.sample.AssetImages
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.model.ImageDetail
import com.github.panpf.sketch.sample.ui.base.LifecycleAndroidViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DecoderTestViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val data = MutableLiveData<Pair<Array<String>, List<ImageDetail>>>()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val headerUserPackageInfo = loadUserAppPackageInfo(true)
            val footerUserPackageInfo = loadUserAppPackageInfo(false)

            val imageDetails = AssetImages.STATICS.plus(AssetImages.ANIMATEDS).plus(
                arrayOf(
                    application1.newResourceUri(drawable.im_placeholder),
                    application1.newResourceUri(drawable.ic_play),
                    footerUserPackageInfo.applicationInfo.publicSourceDir,
                    newAppIconUri(
                        headerUserPackageInfo.packageName,
                        headerUserPackageInfo.versionCode
                    )
                )
            ).map {
                ImageDetail(it, it, null)
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
            data.postValue(titles to imageDetails)
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