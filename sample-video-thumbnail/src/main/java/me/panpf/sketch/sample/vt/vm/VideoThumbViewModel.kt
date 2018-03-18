package me.panpf.sketch.sample.vt.vm

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import me.panpf.sketch.sample.vt.bean.VideoInfo
import me.panpf.sketch.sample.vt.repo.Callback
import me.panpf.sketch.sample.vt.repo.DataRepository

class VideoThumbViewModel(application: Application) : AndroidViewModel(application) {
    val videoList: MutableLiveData<List<VideoInfo>> = MutableLiveData()

    init {
        videoList.value = null
        refreshVideoList()
    }

    fun refreshVideoList() {
        DataRepository.loadVideoList(getApplication(), object : Callback<List<VideoInfo>> {
            override fun onCompleted(t: List<VideoInfo>?) {
                videoList.value = t
            }
        })
    }
}
