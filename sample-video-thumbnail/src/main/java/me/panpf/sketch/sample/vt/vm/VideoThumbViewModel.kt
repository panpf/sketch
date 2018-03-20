package me.panpf.sketch.sample.vt.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import me.panpf.sketch.sample.vt.bean.ApiResponse
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.repo.DataRepository
import me.panpf.sketch.sample.vt.util.NonNullLiveData

class VideoThumbViewModel(application: Application) : AndroidViewModel(application) {
    val videoList: NonNullLiveData<ApiResponse<List<VideoInfo>>> = NonNullLiveData(ApiResponse.loading(null))

    init {
        loadVideoList()
    }

    fun loadVideoList() {
        DataRepository.loadVideoList(getApplication(), videoList)
    }
}
