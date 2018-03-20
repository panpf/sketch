package me.panpf.sketch.sample.vt.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import me.panpf.sketch.sample.vt.bean.ApiResponse
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.repo.DataRepository

class VideoThumbViewModel(application: Application) : AndroidViewModel(application) {
    val videoList: MutableLiveData<ApiResponse<List<VideoInfo>>> = MutableLiveData()

    init {
        loadVideoList()
    }

    fun loadVideoList() {
        DataRepository.loadVideoList(getApplication(), videoList)
    }
}
