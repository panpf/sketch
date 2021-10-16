package me.panpf.sketch.sample.vm

import android.app.Application
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.panpf.sketch.sample.appSettingsService
import me.panpf.sketch.sample.base.LifecycleAndroidViewModel
import me.panpf.sketch.sample.ui.ImageFragmentArgs

class ImageViewModel(application1: Application, private val args: ImageFragmentArgs) :
    LifecycleAndroidViewModel(application1) {

    class Factory(val application1: Application, private val args: ImageFragmentArgs) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ImageViewModel(application1, args) as T
        }
    }

    val imageUrl = MutableLiveData<String>()

    init {
        application1.appSettingsService.showRawImageInDetailEnabled.observe(this) {
            val showRawQualityImage = it == true
            val rawQualityUrl = args.rawQualityUrl
            imageUrl.postValue(if (showRawQualityImage && rawQualityUrl != null) rawQualityUrl else args.normalQualityUrl)
        }
    }
}