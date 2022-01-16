package com.github.panpf.sketch.sample.vm

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.github.panpf.sketch.fetch.newAppIconUri
import com.github.panpf.sketch.fetch.newResourceUri
import com.github.panpf.sketch.sample.AssetImage
import com.github.panpf.sketch.sample.R.drawable
import com.github.panpf.sketch.sample.base.LifecycleAndroidViewModel
import com.github.panpf.sketch.sample.bean.ImageDetail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ImageFormatViewModel(application1: Application) : LifecycleAndroidViewModel(application1) {

    val data = MutableLiveData<Pair<Array<String>, List<ImageDetail>>>()

    init {
        load()
    }

    private fun load() {
        viewModelScope.launch {
            val headerUserPackageInfo = loadUserAppPackageInfo(true)
            val footerUserPackageInfo = loadUserAppPackageInfo(false)

            val imageDetails = AssetImage.IMAGES_FORMAT.plus(
                arrayOf(
                    application1.newResourceUri(drawable.im_placeholder).toString(),
                    application1.newResourceUri(drawable.ic_play).toString(),
                    footerUserPackageInfo.applicationInfo.publicSourceDir,
                    newAppIconUri(
                        headerUserPackageInfo.packageName,
                        headerUserPackageInfo.versionCode
                    ).toString()
                )
            ).map {
                ImageDetail(it, it, null)
            }
            val titles = arrayOf("JPG", "PNG", "GIF", "WEBP", "BMP", "SVG").plus(
                arrayOf(
                    "XML",
                    "VECTOR",
                    "APK_ICON",
                    "APP_ICON",
                )
            )
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